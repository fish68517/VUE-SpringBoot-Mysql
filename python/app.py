from __future__ import annotations

from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
import math
import sys
import tkinter as tk
from tkinter import messagebox, ttk

import numpy as np
from PIL import Image, ImageTk


if getattr(sys, "frozen", False):
    BASE_DIR = Path(getattr(sys, "_MEIPASS", Path(sys.executable).resolve().parent))
    RUNTIME_DIR = Path(sys.executable).resolve().parent
else:
    BASE_DIR = Path(__file__).resolve().parent
    RUNTIME_DIR = BASE_DIR

FORMULA_DIR = BASE_DIR / "Image"
OUTPUT_DIR = RUNTIME_DIR / "generated"
OUTPUT_DIR.mkdir(exist_ok=True)

GROUP_ORDER = [
    "P1",
    "P2",
    "PM",
    "PG",
    "CM",
    "PMM",
    "PMG",
    "PGG",
    "CMM",
    "P4",
    "P4M",
    "P4G",
    "P3",
    "P3M1",
    "P31M",
    "P6",
    "P6M",
]

RESAMPLE = Image.Resampling.LANCZOS if hasattr(Image, "Resampling") else Image.LANCZOS


@dataclass(frozen=True)
class GroupSpec:
    key: str
    base_type: str
    a1: tuple[float, float]
    a2: tuple[float, float]
    rotation_count: int
    # (axis_angle_in_rad, (shift_frac_a1, shift_frac_a2))
    sigma_terms: tuple[tuple[float, tuple[float, float]], ...]
    tile_repeat: int


OBLIQUE_A1 = (160.0, 0.0)
OBLIQUE_A2 = (80.0, 140.0)
RECT_A1 = (180.0, 0.0)
RECT_A2 = (0.0, 160.0)
CENTER_A1 = (190.0, 0.0)
CENTER_A2 = (95.0, 165.0)
SQUARE_A1 = (170.0, 0.0)
SQUARE_A2 = (0.0, 170.0)
HEX_A1 = (180.0, 0.0)
HEX_A2 = (90.0, 155.88457)

GROUP_SPECS = {
    "P1": GroupSpec("P1", "Ls", OBLIQUE_A1, OBLIQUE_A2, 1, (), 3),
    "P2": GroupSpec("P2", "Ls", RECT_A1, RECT_A2, 2, (), 3),
    "PM": GroupSpec("PM", "Ls", RECT_A1, RECT_A2, 1, ((0.0, (0.0, 0.0)),), 3),
    "PG": GroupSpec("PG", "Ls", RECT_A1, RECT_A2, 1, ((0.0, (0.5, 0.0)),), 3),
    "CM": GroupSpec(
        "CM",
        "Ls",
        CENTER_A1,
        CENTER_A2,
        1,
        ((math.pi / 4.0, (0.0, 0.0)), (math.pi / 4.0, (0.5, 0.0))),
        3,
    ),
    "PMM": GroupSpec(
        "PMM",
        "Ls",
        RECT_A1,
        RECT_A2,
        2,
        ((0.0, (0.0, 0.0)), (math.pi / 2.0, (0.0, 0.0))),
        3,
    ),
    "PMG": GroupSpec(
        "PMG",
        "Ls",
        RECT_A1,
        RECT_A2,
        2,
        ((0.0, (0.0, 0.0)), (math.pi / 2.0, (0.5, 0.0))),
        3,
    ),
    "PGG": GroupSpec(
        "PGG",
        "Ls",
        RECT_A1,
        RECT_A2,
        2,
        ((0.0, (0.5, 0.0)), (math.pi / 2.0, (0.0, 0.5))),
        3,
    ),
    "CMM": GroupSpec(
        "CMM",
        "Ls",
        CENTER_A1,
        CENTER_A2,
        2,
        (
            (0.0, (0.0, 0.0)),
            (math.pi / 2.0, (0.0, 0.0)),
            (math.pi / 4.0, (0.0, 0.0)),
            (-math.pi / 4.0, (0.0, 0.0)),
        ),
        3,
    ),
    "P4": GroupSpec("P4", "Ls", SQUARE_A1, SQUARE_A2, 4, (), 3),
    "P4M": GroupSpec("P4M", "Ls", SQUARE_A1, SQUARE_A2, 4, ((0.0, (0.0, 0.0)),), 3),
    "P4G": GroupSpec("P4G", "Ls", SQUARE_A1, SQUARE_A2, 4, ((math.pi / 4.0, (0.5, 0.0)),), 3),
    "P3": GroupSpec("P3", "Ld", HEX_A1, HEX_A2, 3, (), 3),
    "P3M1": GroupSpec("P3M1", "Ld", HEX_A1, HEX_A2, 3, ((0.0, (0.0, 0.0)),), 3),
    "P31M": GroupSpec("P31M", "Ld", HEX_A1, HEX_A2, 3, ((math.pi / 6.0, (0.0, 0.0)),), 3),
    "P6": GroupSpec("P6", "Ld", HEX_A1, HEX_A2, 6, (), 2),
    "P6M": GroupSpec("P6M", "Ld", HEX_A1, HEX_A2, 6, ((0.0, (0.0, 0.0)),), 2),
}


def rotation_matrix(angle: float) -> np.ndarray:
    c = float(math.cos(angle))
    s = float(math.sin(angle))
    return np.array([[c, -s], [s, c]], dtype=np.float32)


def reflection_matrix(axis_angle: float) -> np.ndarray:
    cos2 = float(math.cos(2.0 * axis_angle))
    sin2 = float(math.sin(2.0 * axis_angle))
    return np.array([[cos2, sin2], [sin2, -cos2]], dtype=np.float32)


def build_operations(spec: GroupSpec) -> list[tuple[np.ndarray, np.ndarray, str]]:
    a1 = np.array(spec.a1, dtype=np.float32)
    a2 = np.array(spec.a2, dtype=np.float32)

    base_ops = []
    for k in range(spec.rotation_count):
        angle = 2.0 * math.pi * k / spec.rotation_count
        base_ops.append((rotation_matrix(angle), np.zeros(2, dtype=np.float32), spec.base_type))

    operations = list(base_ops)
    for axis_angle, shift_frac in spec.sigma_terms:
        sigma = reflection_matrix(axis_angle)
        shift = shift_frac[0] * a1 + shift_frac[1] * a2
        for matrix, _, base_type in base_ops:
            operations.append((sigma @ matrix, shift, base_type))

    return operations


def base_ls(local_x: np.ndarray, local_y: np.ndarray) -> tuple[np.ndarray, np.ndarray, np.ndarray]:
    r = np.sqrt(local_x * local_x + local_y * local_y)
    envelope = np.exp(-(r * r) / (110.0**2))
    theta = np.arctan2(local_y, local_x)

    r_val = envelope * (0.5 + 0.5 * np.sin(r * 0.095))
    g_val = envelope * (0.5 + 0.5 * np.cos((local_x + local_y) * 0.05))
    b_val = envelope * (0.5 + 0.5 * np.sin(theta * 6.0 + 0.03 * local_x))
    return r_val, g_val, b_val


def base_ld(local_x: np.ndarray, local_y: np.ndarray) -> tuple[np.ndarray, np.ndarray, np.ndarray]:
    r = np.sqrt(local_x * local_x + local_y * local_y)
    envelope = np.exp(-(r * r) / (120.0**2))
    theta = np.arctan2(local_y, local_x)

    w1 = np.sin(0.085 * local_x)
    w2 = np.sin(0.085 * (-0.5 * local_x + 0.8660254 * local_y))
    w3 = np.sin(0.085 * (-0.5 * local_x - 0.8660254 * local_y))
    interference = (w1 + w2 + w3) / 3.0

    r_val = envelope * (0.5 + 0.5 * interference)
    g_val = envelope * (0.5 + 0.5 * np.cos(0.07 * local_x + 0.03 * local_y))
    b_val = envelope * (0.5 + 0.5 * np.sin(theta * 3.0 + 2.0 * interference))
    return r_val, g_val, b_val


def render_group(group_key: str, width: int, height: int) -> np.ndarray:
    spec = GROUP_SPECS[group_key]
    operations = build_operations(spec)

    x = np.linspace(-width / 2.0, width / 2.0, width, dtype=np.float32)
    y = np.linspace(-height / 2.0, height / 2.0, height, dtype=np.float32)
    grid_x, grid_y = np.meshgrid(x, y)

    a1 = np.array(spec.a1, dtype=np.float32)
    a2 = np.array(spec.a2, dtype=np.float32)
    image_buffer = np.zeros((height, width, 3), dtype=np.float32)

    for m in range(-spec.tile_repeat, spec.tile_repeat + 1):
        for n in range(-spec.tile_repeat, spec.tile_repeat + 1):
            offset = m * a1 + n * a2
            dx = grid_x - offset[0]
            dy = grid_y - offset[1]

            for matrix, shift, base_type in operations:
                local_x = matrix[0, 0] * dx + matrix[0, 1] * dy + shift[0]
                local_y = matrix[1, 0] * dx + matrix[1, 1] * dy + shift[1]

                if base_type == "Ld":
                    rv, gv, bv = base_ld(local_x, local_y)
                else:
                    rv, gv, bv = base_ls(local_x, local_y)

                image_buffer[..., 0] += rv
                image_buffer[..., 1] += gv
                image_buffer[..., 2] += bv

    image_buffer = np.tanh(image_buffer * 0.45)
    image_buffer_8bit = (np.clip(image_buffer, 0.0, 1.0) * 255.0).astype(np.uint8)
    return image_buffer_8bit


class WallpaperGeneratorApp:
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title("Wallpaper Group Pattern Generator")
        self.root.geometry("1250x740")

        self.render_width = 860
        self.render_height = 620
        self.generated_image: Image.Image | None = None
        self.tk_generated: ImageTk.PhotoImage | None = None
        self.formula_preview_tk: ImageTk.PhotoImage | None = None

        self.formula_files = self._load_formula_files()
        self.formula_names = [name for name, _ in self.formula_files]

        self.selected_group = tk.StringVar(value=self.formula_names[0] if self.formula_names else "P1")
        self.status_text = tk.StringVar(value="请选择公式并点击“生成图案”。")

        self._build_ui()
        self._refresh_formula_preview()

    def _load_formula_files(self) -> list[tuple[str, Path]]:
        found = {}
        for image_path in FORMULA_DIR.glob("*.png"):
            key = image_path.stem.upper()
            if key in GROUP_SPECS:
                found[key] = image_path

        files = []
        for key in GROUP_ORDER:
            if key in found:
                files.append((key, found[key]))
        return files

    def _build_ui(self) -> None:
        main = ttk.Frame(self.root, padding=10)
        main.pack(fill=tk.BOTH, expand=True)

        left = ttk.Frame(main, width=330)
        right = ttk.Frame(main)
        left.pack(side=tk.LEFT, fill=tk.Y)
        right.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True)

        ttk.Label(left, text="公式图片（Image目录）", font=("Microsoft YaHei UI", 11, "bold")).pack(
            anchor=tk.W, pady=(0, 8)
        )
        self.combo = ttk.Combobox(
            left,
            state="readonly",
            values=self.formula_names,
            textvariable=self.selected_group,
            width=24,
        )
        self.combo.pack(fill=tk.X, pady=(0, 10))
        self.combo.bind("<<ComboboxSelected>>", lambda _: self._refresh_formula_preview())

        self.preview_label = ttk.Label(left)
        self.preview_label.pack(fill=tk.X, pady=(0, 10))

        btn_frame = ttk.Frame(left)
        btn_frame.pack(fill=tk.X, pady=(0, 10))
        ttk.Button(btn_frame, text="生成图案", command=self.generate_selected).pack(fill=tk.X, pady=(0, 6))
        ttk.Button(btn_frame, text="保存当前图案", command=self.save_generated).pack(fill=tk.X)

        ttk.Label(
            left,
            text="说明：选择某个壁纸群公式，点击“生成图案”即可得到对应平面纹样。",
            wraplength=300,
            justify=tk.LEFT,
            foreground="#444",
        ).pack(fill=tk.X, pady=(8, 0))

        ttk.Label(left, textvariable=self.status_text, wraplength=300, foreground="#0B6").pack(
            fill=tk.X, pady=(12, 0)
        )

        ttk.Label(right, text="生成结果", font=("Microsoft YaHei UI", 11, "bold")).pack(anchor=tk.W, pady=(0, 8))
        self.canvas = tk.Canvas(
            right,
            width=self.render_width,
            height=self.render_height,
            bg="#101216",
            highlightthickness=1,
            highlightbackground="#2b2f38",
        )
        self.canvas.pack(fill=tk.BOTH, expand=False)

    def _refresh_formula_preview(self) -> None:
        key = self.selected_group.get().upper()
        formula_path = dict(self.formula_files).get(key)
        if not formula_path:
            self.preview_label.config(text=f"未找到 {key}.png", image="")
            self.formula_preview_tk = None
            return

        formula_img = Image.open(formula_path).convert("RGB")
        ratio = min(300.0 / formula_img.width, 95.0 / formula_img.height)
        resized = formula_img.resize(
            (max(1, int(formula_img.width * ratio)), max(1, int(formula_img.height * ratio))),
            RESAMPLE,
        )
        self.formula_preview_tk = ImageTk.PhotoImage(resized)
        self.preview_label.config(image=self.formula_preview_tk, text="")

    def generate_selected(self) -> None:
        key = self.selected_group.get().upper()
        if key not in GROUP_SPECS:
            messagebox.showerror("错误", f"未配置该公式：{key}")
            return

        self.status_text.set(f"正在生成 {key} 图案，请稍候...")
        self.root.update_idletasks()

        image_array = render_group(key, self.render_width, self.render_height)
        self.generated_image = Image.fromarray(image_array)
        self.tk_generated = ImageTk.PhotoImage(self.generated_image)

        self.canvas.delete("all")
        self.canvas.create_image(0, 0, anchor=tk.NW, image=self.tk_generated)
        self.status_text.set(f"{key} 生成完成（{datetime.now().strftime('%H:%M:%S')}）")

    def save_generated(self) -> None:
        if self.generated_image is None:
            messagebox.showwarning("提示", "请先生成图案。")
            return

        key = self.selected_group.get().upper()
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        output_path = OUTPUT_DIR / f"{key}_{timestamp}.png"
        self.generated_image.save(output_path)
        self.status_text.set(f"已保存：{output_path.name}")


if __name__ == "__main__":
    root_window = tk.Tk()
    app = WallpaperGeneratorApp(root_window)
    root_window.mainloop()
