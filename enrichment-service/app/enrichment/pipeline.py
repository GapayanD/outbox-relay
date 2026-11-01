from app.enrichment import fraud_score


def enrich(order: dict) -> dict:
    return {**order, "fraud_score": fraud_score.score(order)}
