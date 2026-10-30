CREATE TABLE orders.processed_messages (
  message_id TEXT PRIMARY KEY, processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
