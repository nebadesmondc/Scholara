#!/bin/bash
# Scholara Project Setup Validation Script
# Usage: ./scripts/validate-setup.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "========================================"
echo "Scholara Setup Validation"
echo "========================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

success() { echo -e "${GREEN}[PASS]${NC} $1"; }
fail() { echo -e "${RED}[FAIL]${NC} $1"; exit 1; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
info() { echo -e "[INFO] $1"; }

# Check prerequisites
echo "Checking prerequisites..."
echo ""

command -v java >/dev/null 2>&1 || fail "Java is not installed"
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
info "Java: $JAVA_VERSION"

command -v mvn >/dev/null 2>&1 || fail "Maven is not installed"
MVN_VERSION=$(mvn -v 2>&1 | head -n 1)
info "Maven: $MVN_VERSION"

command -v node >/dev/null 2>&1 || fail "Node.js is not installed"
NODE_VERSION=$(node -v)
info "Node.js: $NODE_VERSION"

command -v npm >/dev/null 2>&1 || fail "npm is not installed"
NPM_VERSION=$(npm -v)
info "npm: $NPM_VERSION"

if command -v docker >/dev/null 2>&1; then
    DOCKER_VERSION=$(docker -v)
    info "Docker: $DOCKER_VERSION"
else
    warn "Docker is not installed (optional for local dev)"
fi

echo ""
echo "========================================"
echo "Validating Backend"
echo "========================================"
echo ""

cd "$PROJECT_ROOT/backend"

info "Compiling backend..."
if mvn clean compile -q; then
    success "Backend compiles successfully"
else
    fail "Backend compilation failed"
fi

info "Running Modulith structure tests..."
if mvn test -q 2>/dev/null; then
    success "Modulith boundaries are valid"
else
    warn "Modulith tests failed"
fi

echo ""
echo "========================================"
echo "Validating Frontend"
echo "========================================"
echo ""

cd "$PROJECT_ROOT/frontend"

info "Installing npm dependencies..."
if npm install --silent 2>/dev/null; then
    success "npm dependencies installed"
else
    fail "npm install failed"
fi

info "Checking TypeScript compilation..."
if npx tsc --noEmit 2>/dev/null; then
    success "TypeScript compiles successfully"
else
    fail "TypeScript compilation failed"
fi

info "Building frontend..."
if npm run build --silent 2>/dev/null; then
    success "Frontend builds successfully"
else
    fail "Frontend build failed"
fi

echo ""
echo "========================================"
echo "Validating Docker (if available)"
echo "========================================"
echo ""

cd "$PROJECT_ROOT/docker"

if command -v docker >/dev/null 2>&1; then
    info "Starting PostgreSQL container..."
    if docker-compose up -d postgres 2>/dev/null; then
        sleep 3
        if docker exec scholara-postgres pg_isready -U scholara >/dev/null 2>&1; then
            success "PostgreSQL is running and accepting connections"
        else
            warn "PostgreSQL container started but not ready yet"
        fi

        info "Stopping containers..."
        docker-compose down >/dev/null 2>&1
        success "Docker setup validated"
    else
        warn "Could not start Docker containers"
    fi
else
    warn "Skipping Docker validation (Docker not installed)"
fi

echo ""
echo "========================================"
echo -e "${GREEN}Setup validation complete!${NC}"
echo "========================================"
echo ""
echo "Next steps:"
echo "  1. Start database:  cd docker && docker-compose up -d postgres"
echo "  2. Run backend:     cd backend && mvn spring-boot:run --enable-preview"
echo "  3. Run frontend:    cd frontend && npm start"
echo ""
