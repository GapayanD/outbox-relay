FROM python:3.12-slim
COPY --from=ghcr.io/astral-sh/uv:latest /uv /bin/uv
COPY . /app
RUN uv pip install --system -r /app/pyproject.toml
CMD ["uvicorn", "--app-dir", "/app", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
