-- SQLite

SELECT product_id, name, total
FROM (SELECT p.product_id, p.name, sum(o.quantity) as total, p.available_from, o.dispatch_date FROM product p, orders o
WHERE p.product_id = o.product_id
AND o.dispatch_date > DATE('now', '-1 year')
AND p.available_from > DATE('now', '-1 month')) c
where c.total < 10;