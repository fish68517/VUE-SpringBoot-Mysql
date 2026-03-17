from __future__ import annotations

import os
from pathlib import Path
from threading import Lock

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel, Field
from PIL import Image

from app import FORMULA_DIR, GROUP_ORDER, GROUP_SPECS, render_group


BASE_DIR = Path(__file__).resolve().parent
GENERATED_DIR = BASE_DIR / "generated_image"
GENERATED_DIR.mkdir(parents=True, exist_ok=True)

DEFAULT_WIDTH = 860
DEFAULT_HEIGHT = 620
render_lock = Lock()

GROUP_FEATURES = {
    "P1": "仅平移重复，图案最自由，方向感和旋转约束最弱。",
    "P2": "含 2 次旋转中心，图案呈明显对向呼应与周期翻转。",
    "PM": "含平行镜像轴，适合形成条带式对称纹样。",
    "PG": "含滑移反射，对称中带有半步错位的流动感。",
    "CM": "中心格+镜像，常见菱形/斜轴重复结构。",
    "PMM": "双向镜像+2次旋转，结构稳定、规整感强。",
    "PMG": "镜像与滑移共存，既规整又带方向推进感。",
    "PGG": "双滑移+2次旋转，纹样有连续错位和节奏变化。",
    "CMM": "中心格上的多镜像组合，常形成网格化花形中心。",
    "P4": "4 次旋转主导，图案有明显十字/方格旋转节律。",
    "P4M": "4 次旋转叠加镜像，中心与轴线结构最均衡。",
    "P4G": "4 次旋转叠加滑移，方格中出现斜向位移动势。",
    "P3": "3 次旋转主导，呈三角晶格与三向辐射感。",
    "P3M1": "3 次旋转+镜像，三角结构更锐利、边界更明确。",
    "P31M": "3 次旋转+另一组镜像布局，中心与边界层次更强。",
    "P6": "6 次旋转主导，六角蜂巢式周期最明显。",
    "P6M": "6 次旋转+镜像，六角花形最完整、装饰性最强。",
}


class GenerateRequest(BaseModel):
    formula_key: str = Field(..., description="公式名称，如 P1/P4M/P6M")
    width: int = Field(DEFAULT_WIDTH, ge=240, le=1920)
    height: int = Field(DEFAULT_HEIGHT, ge=180, le=1920)


api_app = FastAPI(title="Wallpaper Pattern API", version="1.0.0")

origins_env = os.getenv("WALLPAPER_API_CORS", "*")
origins = [x.strip() for x in origins_env.split(",") if x.strip()]
if not origins:
    origins = ["*"]

api_app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

api_app.mount("/generated_image", StaticFiles(directory=str(GENERATED_DIR)), name="generated_image")
api_app.mount("/formula_image", StaticFiles(directory=str(FORMULA_DIR)), name="formula_image")


@api_app.get("/api/wallpaper/health")
def health() -> dict:
    return {"status": "ok"}


@api_app.get("/api/wallpaper/formulas")
def list_formulas() -> dict:
    items = []
    for key in GROUP_ORDER:
        if key not in GROUP_SPECS:
            continue
        formula_name = f"{key}.png"
        formula_path = FORMULA_DIR / formula_name
        if not formula_path.exists():
            continue
        items.append(
            {
                "key": key,
                "formula_image_name": formula_name,
                "formula_image_url": f"/formula_image/{formula_name}",
                "feature": GROUP_FEATURES.get(key, "该公式强调平面周期重复与局部对称。"),
            }
        )

    return {"items": items}


@api_app.post("/api/wallpaper/generate")
def generate_pattern(payload: GenerateRequest) -> dict:
    key = payload.formula_key.strip().upper()
    if key not in GROUP_SPECS:
        raise HTTPException(status_code=400, detail=f"未知公式：{payload.formula_key}")

    output_name = f"{key}.png"
    output_path = GENERATED_DIR / output_name

    with render_lock:
        image_array = render_group(key, payload.width, payload.height)
        Image.fromarray(image_array).save(output_path)

    return {
        "formula_key": key,
        "saved_file_name": output_name,
        "saved_relative_path": f"generated_image/{output_name}",
        "image_url": f"/generated_image/{output_name}",
        "feature": GROUP_FEATURES.get(key, "该公式强调平面周期重复与局部对称。"),
    }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("wallpaper_api:api_app", host="0.0.0.0", port=8001, reload=False)
