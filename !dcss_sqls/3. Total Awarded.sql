-- Total Awarded DCSS Dollars (since the beginning of time).
-- Same PO set and grain as the Ordered column in 1. Ordered vs. Paid.sql.
SELECT COALESCE(SUM(pototal), 0) AS total_task_order_dollars
FROM purchase_order_dcss;
