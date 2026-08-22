-- Ejecutar una sola vez contra la base MySQL local. El contenido se almacena
-- como HTML enriquecido sanitizado y se conserva la columna física existente.
ALTER TABLE blog_posts MODIFY COLUMN resumen VARCHAR(350);
ALTER TABLE blog_posts MODIFY COLUMN contenido_html LONGTEXT NOT NULL;
