---
name: Neo-Retro Arcade
colors:
  surface: '#0b1326'
  surface-dim: '#0b1326'
  surface-bright: '#31394d'
  surface-container-lowest: '#060e20'
  surface-container-low: '#131b2e'
  surface-container: '#171f33'
  surface-container-high: '#222a3d'
  surface-container-highest: '#2d3449'
  on-surface: '#dae2fd'
  on-surface-variant: '#bccbb9'
  inverse-surface: '#dae2fd'
  inverse-on-surface: '#283044'
  outline: '#869585'
  outline-variant: '#3d4a3d'
  surface-tint: '#4ae176'
  primary: '#4be277'
  on-primary: '#003915'
  primary-container: '#22c55e'
  on-primary-container: '#004b1e'
  inverse-primary: '#006e2f'
  secondary: '#ffb0cd'
  on-secondary: '#640039'
  secondary-container: '#aa0266'
  on-secondary-container: '#ffbad3'
  tertiary: '#4dd8f7'
  on-tertiary: '#003640'
  tertiary-container: '#1dbcda'
  on-tertiary-container: '#004754'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#6bff8f'
  primary-fixed-dim: '#4ae176'
  on-primary-fixed: '#002109'
  on-primary-fixed-variant: '#005321'
  secondary-fixed: '#ffd9e4'
  secondary-fixed-dim: '#ffb0cd'
  on-secondary-fixed: '#3e0022'
  on-secondary-fixed-variant: '#8c0053'
  tertiary-fixed: '#acedff'
  tertiary-fixed-dim: '#4cd7f6'
  on-tertiary-fixed: '#001f26'
  on-tertiary-fixed-variant: '#004e5c'
  background: '#0b1326'
  on-background: '#dae2fd'
  surface-variant: '#2d3449'
typography:
  display-lg:
    fontFamily: Sora
    fontSize: 48px
    fontWeight: '800'
    lineHeight: '1.1'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Sora
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.2'
  headline-lg-mobile:
    fontFamily: Sora
    fontSize: 28px
    fontWeight: '700'
    lineHeight: '1.2'
  score-display:
    fontFamily: JetBrains Mono
    fontSize: 24px
    fontWeight: '700'
    lineHeight: '1.0'
    letterSpacing: 0.1em
  body-md:
    fontFamily: Sora
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: '1.0'
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  touch-target: 48px
  container-padding: 20px
---

## Brand & Style
The brand personality is high-energy, nostalgic, and hyper-modern—a "Neo-Retro" aesthetic that blends the addictive simplicity of 90s arcade games with high-end contemporary interface techniques. It targets a mobile-first audience looking for quick bursts of dopamine through competitive gameplay.

The design style is a hybrid of **High-Contrast / Bold** and **Glassmorphism**. By utilizing vibrant neon accents against a deep, void-like background, we create a sense of depth and intensity. The UI should evoke an emotional response of focus and excitement, using "glow" as a primary signifier of interactable elements and game state changes.

## Colors
The palette is built on a high-contrast dark foundation to make the neon elements "pop" as if they are light sources.

- **Primary (Electric Lime):** Reserved for the snake and success states. It represents growth and vitality.
- **Secondary (Hot Pink):** Used for food items, high-score alerts, and critical actions.
- **Tertiary (Cyan):** Used for functional UI accents, borders, and secondary gameplay mechanics (power-ups).
- **Background (Midnight Blue):** A deep, saturated dark tone that provides better depth than pure black.
- **Surface:** A semi-transparent layer used for overlays and menus, facilitating the glassmorphism effect.

## Typography
The typographic system utilizes a dual-font approach to balance modern aesthetics with functional data.

- **Sora** is used for headings and general UI text. Its geometric structure and wide stance feel contemporary and bold.
- **JetBrains Mono** is used for all "technical" data: scores, timers, coordinates, and version numbers. The monospaced nature ensures that jumping numbers don't cause layout shifts during gameplay.

Apply a subtle `0 0 8px` text-shadow to primary headlines in Electric Lime to simulate a neon glow.

## Layout & Spacing
The layout follows a **Fluid Grid** model with an emphasis on "Safe Zones" for thumb-based navigation. 

- **Grid:** A 12-column mobile grid with 16px gutters.
- **Rhythm:** A 4px baseline grid ensures tight, arcade-inspired density while maintaining alignment.
- **Gaming Area:** The snake arena should maintain a fixed aspect ratio (usually 9:16 or 1:1) centered within the fluid container.
- **Interactive Zones:** All buttons must adhere to a minimum 48px touch target. Spacing between menu items should be generous (24px) to prevent accidental taps during intense sessions.

## Elevation & Depth
Depth is created through **Glassmorphism** and **Luminescent Borders** rather than traditional drop shadows.

- **Layer 0 (Background):** Solid Midnight Blue (#0F172A).
- **Layer 1 (Cards/Overlays):** Semi-transparent surface (30, 41, 59, 0.7) with a 16px backdrop blur. 
- **Layer 2 (Modals):** Same as Layer 1 but with a 1px solid Cyan (#06B6D4) border and a 4px outer glow (box-shadow).
- **Interactive Depth:** When a button is pressed, the glow intensity should increase (bloom effect) rather than the element moving "down."

## Shapes
The design system uses a **Rounded** language to soften the high-contrast neon tech vibe, making the game feel approachable and "toy-like."

- **Primary Radius:** 0.5rem (8px) for cards and menu buttons.
- **Snake Segments:** Rounded-lg (16px) to create a smooth, organic feel for the primary character.
- **Food Items:** Circular (Pill-shaped) to distinguish them from the structured rectangular UI.

## Components

### Buttons
Primary buttons use a solid Electric Lime background with black text. Secondary buttons use a transparent background with a 2px Cyan border and Cyan text. All buttons have a high-intensity "Bloom" glow on active states.

### Cards & Overlays
Utilize the Glassmorphism specification: backdrop-blur (16px), 70% opacity surface color, and a subtle top-down gradient. The border should be a thin 1px stroke at 30% opacity of the Cyan color.

### Scoreboard
The scoreboard is a floating glass element at the top of the screen. Use JetBrains Mono for the numerals. The "High Score" label should be in Hot Pink to signal importance.

### Inputs (D-Pad/Joystick)
On-screen controls should be ultra-minimal. Use large, semi-transparent circular touch zones with Cyan outlines. Provide haptic feedback for every directional change.

### Chips/Badges
Small, pill-shaped elements used for difficulty levels (e.g., "Hard", "Insane"). These use high-saturation backgrounds (Hot Pink) with white text to demand attention.