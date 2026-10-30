from app.enrichment.address_normalizer import normalize


def test_collapses_whitespace():
    assert normalize("  12  main   st ") == "12 Main St"
