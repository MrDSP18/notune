#!/usr/bin/env bash
# NØTUNE Cloudflare Production Deployment Helper Script
# Executes Wrangler authentication check, D1 database setup, remote schema migration, and worker deployment

set -e

echo "======================================================="
echo "   NØTUNE Cloudflare Production Deployment Helper    "
echo "======================================================="

# 1. Check Wrangler CLI Installation
if ! command -v npx &> /dev/null; then
    echo "❌ Error: npx is not installed. Please install Node.js and npm."
    exit 1
fi

echo "🔍 1. Verifying Cloudflare Wrangler Authentication..."
npx wrangler whoami || {
    echo "⚠️ Not logged in to Cloudflare. Opening browser for wrangler login..."
    npx wrangler login
}

echo "✅ Wrangler authenticated."

# 2. D1 Database Check & Remote Setup
echo "🗄️ 2. Executing Cloudflare D1 Database Migration..."
echo "Running schema.sql against remote D1 database 'notune-db'..."

npx wrangler d1 execute notune-db --remote --file=schema.sql || {
    echo "⚠️ Remote D1 database execute failed or notune-db does not exist yet."
    echo "To create the D1 database for your account, run:"
    echo "   npx wrangler d1 create notune-db"
    echo "Then copy the returned database_id into wrangler.toml before re-running this script."
    exit 1
}

echo "✅ D1 Database schema updated."

# 3. Worker Deployment
echo "🚀 3. Deploying NØTUNE Edge Worker..."
npx wrangler deploy

echo "======================================================="
echo "🎉 NØTUNE Production Backend Successfully Deployed!"
echo "======================================================="
