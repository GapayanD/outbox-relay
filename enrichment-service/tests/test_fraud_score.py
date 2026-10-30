from app.enrichment.fraud_score import score
def test_score_in_range():
    assert 0.0 <= score({}) <= 1.0
