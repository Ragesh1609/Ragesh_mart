Migration numbering per Section 14 of the spec.
V1__init_schema.sql = baseline schema (mirrors src/main/resources/schema.sql).
Future changes: V2__add_reviews_table.sql, V3__add_index_orders_status.sql, etc.
Never hand-edit a live table — always add a new numbered file.
