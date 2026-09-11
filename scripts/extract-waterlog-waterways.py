"""Extract the waterway lines needed by the offline flood-warning map.

Development-only dependency: ``python -m pip install osmium``.
The generated raw GeoJSON files stay under the git-ignored map-source folder.
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any

try:
    import osmium
except ImportError as exc:  # pragma: no cover - developer environment guard
    raise SystemExit(
        "缺少 Python osmium 包，请先执行：python -m pip install osmium"
    ) from exc


BBOX = (105.1171875, 28.613459424004414, 109.6875, 31.353636941500987)
WATERWAY_TYPES = {"river", "stream", "canal"}


def intersects_bbox(coordinates: list[list[float]]) -> bool:
    min_lng, min_lat, max_lng, max_lat = BBOX
    return any(
        min_lng <= float(lng) <= max_lng and min_lat <= float(lat) <= max_lat
        for lng, lat, *_ in coordinates
    )


class WaterwayHandler(osmium.SimpleHandler):
    def __init__(self) -> None:
        super().__init__()
        self.factory = osmium.geom.GeoJSONFactory()
        self.features: list[dict[str, Any]] = []
        self.invalid_geometry_count = 0

    def way(self, way: Any) -> None:
        waterway = way.tags.get("waterway")
        if waterway not in WATERWAY_TYPES:
            return

        try:
            geometry = json.loads(self.factory.create_linestring(way))
        # Regional extracts occasionally contain an incomplete boundary way. It
        # must not abort the whole offline dataset; count and skip that feature.
        except Exception:  # noqa: BLE001 - pyosmium exposes backend exception types
            self.invalid_geometry_count += 1
            return

        coordinates = geometry.get("coordinates") or []
        if len(coordinates) < 2 or not intersects_bbox(coordinates):
            return

        properties = {
            "name": way.tags.get("name") or None,
            "name:zh": way.tags.get("name:zh") or None,
            "waterway": waterway,
        }
        self.features.append(
            {
                "type": "Feature",
                "id": f"w{way.id}",
                "properties": properties,
                "geometry": geometry,
            }
        )


def extract(source: Path, output: Path) -> None:
    if not source.is_file():
        raise SystemExit(f"PBF 文件不存在：{source}")

    handler = WaterwayHandler()
    handler.apply_file(str(source), locations=True)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        json.dumps(
            {"type": "FeatureCollection", "features": handler.features},
            ensure_ascii=False,
            separators=(",", ":"),
        ),
        encoding="utf-8",
    )
    print(
        f"{source.name}: waterways={len(handler.features)}, "
        f"invalid={handler.invalid_geometry_count}, output={output}"
    )


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()
    extract(args.source.resolve(), args.output.resolve())


if __name__ == "__main__":
    main()
