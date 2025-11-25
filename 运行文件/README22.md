# Common Layout Components

This directory contains the common layout components for the Little Shark Fitness Management System.

## Components

### Layout.vue
The main layout wrapper component that provides the overall structure with header, sidebar, and main content area.

**Usage:**
```vue
<template>
  <Layout>
    <YourPageContent />
  </Layout>
</template>

<script setup>
import Layout from '@/components/common/Layout.vue'
</script>
```

**Features:**
- Responsive design with collapsible sidebar
- Integrates with app store for sidebar state management
- Includes Header and Sidebar components automatically

### Header.vue
The top navigation bar component.

**Features:**
- Logo and application name
- Sidebar toggle button
- Integrated search bar
- User avatar dropdown with profile and logout options
- Notification icon (placeholder for future implementation)
- Responsive design (hides search bar and username on mobile)

### Sidebar.vue
The side navigation menu component with role-based menu items.

**Features:**
- Role-based menu items:
  - **User menu**: Home, Resources, My Collections, Training Plans, Community, Check-in, Diet Records
  - **Coach menu**: Dashboard, Students, Create Plan, Analytics, My Content
  - **Admin menu**: Dashboard, User Management, Resource Management, Moderation
- Active route highlighting
- Collapsible functionality
- Smooth transitions
- Custom scrollbar styling

### SearchBar.vue
A reusable search component with type filtering.

**Features:**
- Search input with icon
- Type filter dropdown (All, Resources, Posts, Coaches)
- Search button
- Enter key support
- Emits search event to parent component

**Usage:**
```vue
<SearchBar @search="handleSearch" />

<script setup>
const handleSearch = (searchData) => {
  console.log(searchData.query, searchData.type)
}
</script>
```

### Footer.vue
The footer component with links and social icons.

**Features:**
- Copyright information
- Links to about, contact, terms, and privacy pages
- Social media icons (WeChat, Weibo, Email)
- Responsive design

## Integration Example

To use the layout in your views, wrap your content with the Layout component:

```vue
<template>
  <Layout>
    <div class="page-content">
      <h1>Your Page Title</h1>
      <p>Your page content here...</p>
    </div>
  </Layout>
</template>

<script setup>
import Layout from '@/components/common/Layout.vue'
</script>

<style scoped>
.page-content {
  /* Your page styles */
}
</style>
```

## Responsive Breakpoints

- **Mobile**: < 768px
- **Tablet**: 768px - 1024px
- **Desktop**: > 1024px

## State Management

The layout components integrate with Pinia stores:
- **authStore**: For user information and authentication state
- **appStore**: For sidebar collapse state and loading indicators

## Styling

All components use scoped styles and Element Plus components for consistency. The color scheme follows:
- Sidebar background: `#304156`
- Header background: `#fff`
- Main content background: `#f0f2f5`
- Primary color: `#409eff` (Element Plus default)
