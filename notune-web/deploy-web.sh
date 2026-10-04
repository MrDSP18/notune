#!/usr/bin/env bash
set -e

echo "======================================================="
echo "   NØTUNE Web Platform Cloudflare Deployment Helper    "
echo "======================================================="

# Auto-source NVM if installed
export NVM_DIR="$HOME/.nvm"
if [ -s "$NVM_DIR/nvm.sh" ]; then
  . "$NVM_DIR/nvm.sh"
  nvm use 22 2>/dev/null || true
fi

echo "📦 Node.js Version: $(node -v 2>/dev/null || echo 'Not Found')"
echo "🔍 1. Verifying Cloudflare Wrangler Authentication..."

if ! npx wrangler whoami > /dev/null 2>&1; then
  echo "⚠️ Not logged in to Cloudflare. Opening browser for wrangler login..."
  npx wrangler login
fi

echo "✅ Wrangler authenticated."
echo "🚀 2. Deploying NØTUNE Web Platform to Cloudflare Pages..."

npx wrangler pages deploy . --project-name=notune-web --branch=main

echo "======================================================="
echo "✅ NØTUNE Web Platform Deployment Complete!"
echo "   Live URL: https://notune-web.pages.dev"
echo "   (Bound to Custom Domain: https://notune.app)"
echo "======================================================="
