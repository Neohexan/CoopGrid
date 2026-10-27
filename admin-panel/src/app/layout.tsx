import { Ballet, JetBrains_Mono } from "next/font/google";
import { Header } from "@/components/Header/Header";
import "./globals.css";

const ballet = Ballet({
  subsets: ["latin"],
  variable: "--font-ballet",
});

const jetbrainsMono = JetBrains_Mono({
  subsets: ["latin"],
  variable: "--font-tech-mono",
});

export const metadata = {
  title: "CoopGrid Admin",
  description: "Admin panel for CoopGrid",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className={`${ballet.variable} ${jetbrainsMono.variable}`}>
      <body className="bg-[#111111] text-[#f5f5f5] antialiased">
        <Header />
        {children}
      </body>
    </html>
  );
}
