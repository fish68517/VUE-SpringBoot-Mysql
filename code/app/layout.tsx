import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: '日程清单｜专注每一天',
  description: '简洁好用的待办与专注数据管理网站。',
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="zh-CN"><body>{children}</body></html>;
}
