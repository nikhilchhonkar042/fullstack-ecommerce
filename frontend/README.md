# Frontend Application

Modern Next.js-based frontend for the e-commerce platform with React, TypeScript, and responsive design.

## Overview

The frontend application provides:

- Product browsing and search
- Shopping cart management
- Secure checkout
- Order tracking
- User account management
- Real-time notifications
- Responsive mobile design

## Tech Stack

- **Framework**: Next.js 14+
- **Language**: TypeScript
- **UI Framework**: React 18+
- **Styling**: Tailwind CSS
- **State Management**: Redux Toolkit / Context API
- **HTTP Client**: Axios
- **WebSocket**: Socket.io / native WebSocket
- **Charts**: Chart.js or Recharts
- **Form Handling**: React Hook Form
- **Validation**: Zod / Yup

## Project Structure

```
frontend/
├── app/                          # Next.js app directory
│   ├── layout.tsx               # Root layout
│   ├── page.tsx                 # Home page
│   ├── products/                # Products routes
│   ├── checkout/                # Checkout routes
│   ├── orders/                  # Order management
│   └── account/                 # User account
├── components/                   # Reusable components
│   ├── Header.tsx
│   ├── ProductCard.tsx
│   ├── Cart/
│   └── ...
├── services/                    # API integration
│   ├── api-client.ts
│   ├── order-service.ts
│   ├── payment-service.ts
│   └── ...
├── hooks/                       # Custom React hooks
│   ├── useAuth.ts
│   ├── useCart.ts
│   └── ...
├── store/                       # Redux store
│   ├── slices/
│   └── middleware/
├── utils/                       # Utility functions
├── types/                       # TypeScript types
├── styles/                      # Global styles
├── public/                      # Static assets
└── tests/                       # Test files
```

## Getting Started

### Prerequisites

- Node.js 16.x or higher
- npm or yarn package manager

### Installation

```bash
cd frontend

# Install dependencies
npm install

# Or using yarn
yarn install
```

### Environment Variables

Create `.env.local`:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
NEXT_PUBLIC_WS_URL=ws://localhost:8080
NEXT_PUBLIC_STRIPE_PUBLIC_KEY=pk_test_xxx
NEXT_PUBLIC_ENVIRONMENT=development
```

### Development

```bash
# Start development server
npm run dev

# Server runs at http://localhost:3000
```

### Production Build

```bash
# Build for production
npm run build

# Start production server
npm start
```

## Features

### 1. Product Catalog

```tsx
// Components/ProductCard.tsx
export const ProductCard = ({ product }: Props) => {
  return (
    <div className="product-card">
      <img src={product.image} alt={product.name} />
      <h3>{product.name}</h3>
      <p className="price">${product.price}</p>
      <button onClick={() => addToCart(product)}>Add to Cart</button>
    </div>
  );
};
```

### 2. Shopping Cart

```tsx
// hooks/useCart.ts
export const useCart = () => {
  const [cart, setCart] = useState<CartItem[]>([]);

  const addItem = (product: Product, quantity: number) => {
    // Add to cart logic
  };

  const removeItem = (productId: string) => {
    // Remove from cart
  };

  const updateQuantity = (productId: string, quantity: number) => {
    // Update quantity
  };

  return { cart, addItem, removeItem, updateQuantity };
};
```

### 3. Order Management

```tsx
// app/orders/page.tsx
export default async function OrdersPage() {
  const session = await getSession();
  const orders = await orderService.getCustomerOrders(session.userId);

  return (
    <div className="orders-container">
      {orders.map((order) => (
        <OrderCard key={order.id} order={order} />
      ))}
    </div>
  );
}
```

### 4. Real-Time Notifications

```tsx
// hooks/useNotifications.ts
export const useNotifications = () => {
  useEffect(() => {
    const ws = new WebSocket(process.env.NEXT_PUBLIC_WS_URL);

    ws.onmessage = (event) => {
      const notification = JSON.parse(event.data);
      showNotification(notification);
    };

    return () => ws.close();
  }, []);
};
```

### 5. Checkout Process

```tsx
// app/checkout/page.tsx
export default function CheckoutPage() {
  const { cart } = useCart();
  const [formData, setFormData] = useState<CheckoutForm>({});

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const result = await paymentService.processPayment({
      items: cart,
      ...formData,
    });

    if (result.success) {
      router.push(`/orders/${result.orderId}`);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* Checkout form fields */}
      <ShippingForm />
      <PaymentForm />
      <button type="submit">Place Order</button>
    </form>
  );
}
```

## API Integration

### API Client Setup

```typescript
// services/api-client.ts
import axios from "axios";

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add JWT token to requests
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
```

### Service Methods

```typescript
// services/order-service.ts
export const orderService = {
  createOrder: (request: CreateOrderRequest) =>
    apiClient.post("/orders", request),

  getOrder: (orderId: string) => apiClient.get(`/orders/${orderId}`),

  getOrders: (page = 0, size = 20) =>
    apiClient.get("/orders", { params: { page, size } }),

  streamOrderStatus: (orderId: string) => {
    const eventSource = new EventSource(
      `${process.env.NEXT_PUBLIC_API_BASE_URL}/orders/${orderId}/status/stream`
    );
    return eventSource;
  },
};
```

## State Management

### Redux Setup

```typescript
// store/slices/cartSlice.ts
import { createSlice } from "@reduxjs/toolkit";

export const cartSlice = createSlice({
  name: "cart",
  initialState: {
    items: [],
    total: 0,
  },
  reducers: {
    addToCart: (state, action) => {
      state.items.push(action.payload);
      state.total += action.payload.price;
    },
    removeFromCart: (state, action) => {
      state.items = state.items.filter((item) => item.id !== action.payload);
    },
  },
});

export default cartSlice.reducer;
```

## Testing

```bash
# Run unit tests
npm run test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage

# Run E2E tests
npm run test:e2e
```

### Test Example

```typescript
// __tests__/components/ProductCard.test.tsx
describe("ProductCard", () => {
  it("renders product information", () => {
    const product = {
      id: "1",
      name: "Test Product",
      price: 99.99,
    };

    render(<ProductCard product={product} />);

    expect(screen.getByText("Test Product")).toBeInTheDocument();
    expect(screen.getByText("$99.99")).toBeInTheDocument();
  });

  it("calls addToCart on button click", () => {
    const addToCart = jest.fn();
    render(<ProductCard product={product} onAddToCart={addToCart} />);

    fireEvent.click(screen.getByText("Add to Cart"));
    expect(addToCart).toHaveBeenCalled();
  });
});
```

## Performance Optimization

### Image Optimization

```tsx
import Image from "next/image";

export const ProductImage = ({ src, alt }: Props) => (
  <Image
    src={src}
    alt={alt}
    width={300}
    height={300}
    priority={isPriority}
    placeholder="blur"
    blurDataURL={blurUrl}
  />
);
```

### Code Splitting

```tsx
// Dynamic imports for large components
const HeavyChart = dynamic(() => import("./components/Chart"), {
  loading: () => <LoadingSpinner />,
});
```

### API Caching

```typescript
// SWR hook for data fetching with caching
import useSWR from "swr";

export const useProduct = (id: string) => {
  const { data, error } = useSWR(`/api/products/${id}`, fetcher, {
    revalidateOnFocus: false,
  });

  return { product: data, isLoading: !error && !data, error };
};
```

## Accessibility

- Semantic HTML structure
- ARIA labels and roles
- Keyboard navigation support
- Color contrast compliance (WCAG AA)
- Screen reader friendly

## Security

- Environment variables for sensitive data
- HTTPS only in production
- CSRF protection
- XSS prevention (built-in to Next.js)
- Content Security Policy headers
- Secure cookie handling

## Deployment

### Docker Build

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/public ./public
COPY package*.json ./
EXPOSE 3000
CMD ["npm", "start"]
```

### Kubernetes Deployment

```bash
kubectl apply -f k8s/frontend-deployment.yaml
kubectl expose deployment frontend --type=LoadBalancer --port=80 --target-port=3000
```

### Environment-Specific Configuration

```bash
# Development
npm run dev

# Staging
NODE_ENV=production npm run build
npm start

# Production
NEXT_PUBLIC_ENVIRONMENT=production npm run build
npm start
```

## Monitoring

- **Sentry**: Error tracking and monitoring
- **LogRocket**: Session replay and logging
- **Google Analytics**: User behavior tracking
- **Vercel Analytics**: Core Web Vitals

## Common Tasks

### Add New Page

```bash
# Create new route
mkdir -p app/new-feature
touch app/new-feature/page.tsx

# Add to navigation
# Update navigation component to include link
```

### Create New Component

```bash
# Create component file
touch components/NewComponent.tsx

# Add TypeScript types
touch types/NewComponent.types.ts

# Add tests
touch __tests__/components/NewComponent.test.tsx
```

### Update API Integration

```bash
# Edit service file
# services/[service-name].ts

# Update types if needed
# types/api.types.ts

# Add tests
# __tests__/services/[service-name].test.ts
```

## Troubleshooting

| Issue                  | Solution                                 |
| ---------------------- | ---------------------------------------- |
| API connection refused | Check API_BASE_URL environment variable  |
| Styles not loading     | Clear .next folder and rebuild           |
| Hot reload not working | Check if next.config.js is valid         |
| Module not found       | Run `npm install` and restart dev server |

## Contributing

See `CONTRIBUTING.md` for guidelines.

## Resources

- [Next.js Documentation](https://nextjs.org/docs)
- [React Documentation](https://react.dev)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)

## License

Proprietary - All rights reserved
