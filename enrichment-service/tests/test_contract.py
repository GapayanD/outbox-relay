import json, pathlib
SCHEMA = pathlib.Path(__file__).parents[2] / "contracts/events/order.created.v1.json"
def test_schema_is_valid_json():
    assert json.loads(SCHEMA.read_text())["title"] == "order.created.v1"
