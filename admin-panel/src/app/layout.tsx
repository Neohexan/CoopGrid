import { Ballet, JetBrains_Mono } from "next/font/google";
import "./globals.css";
import { AppProvider } from "@/components/AppProvider";

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
      <body className="bg-[#f5f5f5] text-[#111111] antialiased min-h-screen flex flex-col">
        {/* The AppProvider wraps the whole site. If a user isn't logged in, 
            it intercepts rendering and drops the Login page here. */}
        <AppProvider>
          {children}
        </AppProvider>
      </body>
    </html>
  );
}
