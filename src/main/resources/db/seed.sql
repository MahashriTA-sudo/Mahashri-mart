INSERT INTO users (name, email, password_hash, role) VALUES
('Mahashri Admin', 'admin@mahashri.com', '$2a$12$hMeJtnORtogZZDgs0Dzpy.NwYkf1eogwNtaNEPxaHavVrROuK5dw2', 'ADMIN'),
('Aarav Organics', 'seller1@mahashri.com', '$2a$12$hMeJtnORtogZZDgs0Dzpy.NwYkf1eogwNtaNEPxaHavVrROuK5dw2', 'SELLER'),
('Meera Homeware', 'seller2@mahashri.com', '$2a$12$hMeJtnORtogZZDgs0Dzpy.NwYkf1eogwNtaNEPxaHavVrROuK5dw2', 'SELLER'),
('Riya Sharma', 'buyer1@mahashri.com', '$2a$12$hMeJtnORtogZZDgs0Dzpy.NwYkf1eogwNtaNEPxaHavVrROuK5dw2', 'BUYER'),
('Kabir Patel', 'buyer2@mahashri.com', '$2a$12$hMeJtnORtogZZDgs0Dzpy.NwYkf1eogwNtaNEPxaHavVrROuK5dw2', 'BUYER');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) VALUES
(2, 'Almond Granola', 'Small-batch toasted oats, almonds, and jaggery for an easy morning start.', 349.00, 24, 'Pantry', 'https://images.unsplash.com/photo-1517093728432-a0440f8d45af?auto=format&fit=crop&w=900&q=80'),
(2, 'Wild Forest Honey', 'Raw, unfiltered honey gathered from wild forest blooms.', 499.00, 18, 'Pantry', 'https://images.unsplash.com/photo-1471943311424-646960669fbc?auto=format&fit=crop&w=900&q=80'),
(2, 'Ceylon Cinnamon', 'Aromatic, delicate cinnamon quills for chai, bakes, and slow cooking.', 229.00, 30, 'Pantry', 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=900&q=80'),
(2, 'Cold-Pressed Sesame Oil', 'Golden sesame oil pressed in small batches without additives.', 389.00, 14, 'Kitchen', 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=900&q=80'),
(3, 'Terracotta Serving Bowl', 'Hand-shaped terracotta bowl with a warm matte finish.', 699.00, 9, 'Home', 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?auto=format&fit=crop&w=900&q=80'),
(3, 'Linen Kitchen Towels', 'Set of two absorbent, everyday linen towels in natural tones.', 549.00, 16, 'Home', 'https://images.pexels.com/photos/4794899/pexels-photo-4794899.jpeg'),
(3, 'Neem Wood Comb', 'Smooth, hand-finished neem wood comb for a simple daily ritual.', 199.00, 28, 'Wellness', 'https://images.unsplash.com/photo-1616394584738-fc6e612e71b9?auto=format&fit=crop&w=900&q=80'),
(3, 'Handwoven Cotton Throw', 'Breathable handwoven cotton throw for sofas, beds, and reading corners.', 1299.00, 7, 'Home', 'https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?auto=format&fit=crop&w=900&q=80');