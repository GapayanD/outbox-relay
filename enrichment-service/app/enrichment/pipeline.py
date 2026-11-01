from app.enrichment import fraud_score


def _require_non_empty_string(
    order: dict,
    field: str,
) -> str:

    value = order.get(field)

    if not isinstance(value, str) or not value.strip():
        raise ValueError(f"Invalid field: {field}")

    return value


def enrich(order: dict) -> dict:
    return {**order, "fraud_score": fraud_score.score(order)}
