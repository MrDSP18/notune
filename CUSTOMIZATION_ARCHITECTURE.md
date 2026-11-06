# NØTUNE Studio & Customization Architecture

## Overview

The **NØTUNE Customization Platform** provides deep, centralized styling and layout control across the entire application without hard-coding decorative values into individual Compose screens.

---

## Core Components

1. **`NotuneCustomizationRepository`**:
   - DataStore-backed persistence for `NotuneCustomizationConfig`.
   - Supports 6 configuration domains:
     - **Theme**: Presets (NØTUNE Red #FF0031, OLED Dark, Mono Minimal, Cyber Neon, Glass Frost, Sunset Amber, Custom), primary/secondary/background/surface colors.
     - **Typography**: Font scale, monospace mode, letter-spacing scale.
     - **Geometry**: Shape presets (SHARP, SOFT, PILL, CIRCLE, TECHNICAL), corner radii.
     - **Effects**: Blur intensity, glassmorphism, reduced motion, glow effects.
     - **Player**: Artwork toggle, lyrics toggle, visualizer toggle, queue preview toggle, AI button toggle.
     - **Components**: Recently Played, For You, Music DNA, Rooms, Discovery toggles.

2. **JSON Export & Import**:
   - `exportThemeToJson()` serializes the active configuration into structured JSON.
   - `importThemeFromJson()` parses and validates imported theme JSON strings against non-configuration code execution.

3. **`NotuneStudioScreen`**:
   - Comprehensive Studio UI with 10 dedicated sections: IDENTITY, COLORS, TYPOGRAPHY, GEOMETRY, EFFECTS, PLAYER, NAVIGATION, COMPONENTS, MOTION, ACCESSIBILITY.
