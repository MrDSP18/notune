# NØTUNE — DESIGN SYSTEM

Version: 1.0
Status: Production Design Specification
Product: NØTUNE
Platform: Android
Design Language: Futuristic Music Operating System
Primary Experience: Music + AI + Social + Real-Time Listening

---

# 1. PRODUCT IDENTITY

NØTUNE is not a conventional music-player clone.

NØTUNE is a futuristic Music Operating System combining:

- Music playback
- Local music library
- Music discovery
- AI music intelligence
- Music identity
- Social music
- Friends
- Messaging
- Collaborative playlists
- Real-time listening
- Listen Together rooms
- Couple Mode
- Music stories
- Music challenges
- Music polls
- Music compatibility
- AI DJ
- AI radio
- AI playlists
- Personalization
- Widgets
- Offline music
- Privacy controls
- Deep customization

The interface must make music feel like the central operating layer of the application.

NØTUNE should feel:

- Futuristic
- Premium
- Technical
- Intelligent
- Minimal
- Emotional
- Fast
- Immersive
- Personal
- Social
- Original

NØTUNE must NOT visually copy Spotify, Apple Music, YouTube Music, Nothing OS, or another existing music application.

References may inspire principles, but the final visual language must remain original.

---

# 2. CORE DESIGN PHILOSOPHY

The interface follows six principles.

## 2.1 Music First

Artwork, song identity, playback state, lyrics, queue and musical context are always visually prioritized.

## 2.2 Intelligence Everywhere

AI should feel integrated into the operating experience rather than isolated into a chatbot screen.

## 2.3 Social Without Noise

Social interactions must feel native to music.

Avoid generic social-media UI patterns where music context can be preserved instead.

## 2.4 Personalization

The user should be able to make NØTUNE feel like their own system.

Customization applies to:

- Theme
- Typography
- Logo
- Player
- Cards
- Navigation
- Gestures
- Widgets
- Motion

## 2.5 Information With Hierarchy

Do not display everything simultaneously.

Use:

Primary → Secondary → Context → Advanced

Visual hierarchy must remain clear.

## 2.6 Futuristic But Usable

Futuristic design must never reduce usability.

Avoid:

- Excessive glow
- Decorative animations everywhere
- Tiny unreadable text
- Unnecessary 3D
- Excessive glass
- Excessive gradients
- Hidden controls
- Ambiguous icons

---

# 3. VISUAL CHARACTER

NØTUNE's default visual identity is:

- OLED-first
- High contrast
- Sharp geometry
- Technical typography
- Artwork-driven surfaces
- Controlled accent color
- Subtle motion
- Dense but readable information
- Large visual hierarchy
- Precision alignment

Default visual atmosphere:

Black + white + restrained red accent.

The default identity must remain recognizable even when users select another theme.

---

# 4. DESIGN TOKENS

All UI must be driven by centralized design tokens.

Do not hardcode visual values throughout screens.

Tokens must include:

- Colors
- Typography
- Font sizes
- Font weights
- Spacing
- Corner radius
- Elevation
- Border thickness
- Icon sizes
- Animation durations
- Motion curves
- Blur intensity
- Artwork treatment
- Navigation dimensions

The design system must support runtime theme switching.

---

# 5. COLOR SYSTEM

The color system is semantic rather than component-specific.

Required semantic tokens:

- Background Primary
- Background Secondary
- Background Tertiary
- Surface
- Surface Elevated
- Surface Glass
- Surface Strong
- Text Primary
- Text Secondary
- Text Tertiary
- Text Disabled
- Accent
- Accent Secondary
- Success
- Warning
- Error
- Info
- Divider
- Outline
- Scrim
- Player Background
- Artwork Overlay
- Selection
- Pressed
- Focused

Never directly reference a hardcoded color from individual screens when a semantic token exists.

---

# 6. DEFAULT NØTUNE PURE THEME

Default theme:

NØTUNE PURE

Visual language:

- OLED black
- White typography
- Technical red accent
- Sharp cards
- Thin borders
- High contrast
- Minimal shadows
- Controlled glow
- Geometric layouts
- Dense information
- Technical indicators

Default background should prioritize true/dark OLED surfaces.

Red should be an accent, not the dominant screen color.

---

# 7. THEME SYSTEM

NØTUNE must provide at least 20 complete visual themes.

A theme is NOT simply a color palette.

Every theme may alter:

- Background
- Surface design
- Card geometry
- Typography treatment
- Icon treatment
- Navigation
- Player
- Artwork treatment
- Animation
- Motion speed
- Border treatment
- Glow
- Blur
- Widget styling
- Button styling
- Lyrics styling
- Social surfaces
- Profile presentation

---

# 8. THEME 01 — NØTUNE PURE

Mood:

Technical / Minimal / Original

Visual:

- OLED black
- White
- Red accent
- Sharp geometry
- Thin outlines
- Technical typography

Motion:

- Fast
- Precise
- Mechanical

Player:

- High contrast
- Minimal controls
- Strong artwork focus

---

# 9. THEME 02 — MIDNIGHT

Mood:

Cinematic / Calm

Visual:

- Midnight blue-black
- Soft blue
- Deep gradients
- Cinematic artwork

Motion:

- Slow
- Smooth
- Fade-based

Player:

- Large artwork
- Soft atmospheric background

---

# 10. THEME 03 — AURORA

Mood:

Organic / Intelligent

Visual:

- Green
- Teal
- Purple
- Aurora gradients

Motion:

- Fluid
- Slowly shifting backgrounds
- Soft transitions

Do not animate continuously in ways that drain battery.

---

# 11. THEME 04 — MONO

Mood:

Editorial / Minimal

Visual:

- Black
- White
- Grey
- Strong typography
- No unnecessary decoration

Cards:

- Flat
- Editorial
- Structured

---

# 12. THEME 05 — NEBULA

Mood:

Space / Exploration

Visual:

- Deep black
- Purple
- Blue
- Subtle cosmic effects

Artwork:

- Immersive

Motion:

- Slow orbital movement

Effects must remain subtle.

---

# 13. THEME 06 — OCEAN

Mood:

Fluid / Relaxed

Visual:

- Dark navy
- Cyan
- Blue

Motion:

- Wave-inspired

Player:

- Waveform-inspired progress

---

# 14. THEME 07 — EMBER

Mood:

Energy / Heat

Visual:

- Charcoal
- Black
- Orange
- Red

Motion:

- Fast
- Heat-like transitions

---

# 15. THEME 08 — FOREST

Mood:

Organic / Calm

Visual:

- Deep green
- Black
- Muted natural tones

Motion:

- Soft
- Organic

---

# 16. THEME 09 — LAVENDER

Mood:

Elegant / Emotional

Visual:

- Dark violet
- Lavender
- Soft highlights

Typography:

- Elegant but readable

---

# 17. THEME 10 — SOLAR

Mood:

Energy / Optimism

Visual:

- Black
- Warm yellow
- Gold-like highlights

Motion:

- Radial
- Energetic

---

# 18. THEME 11 — GLASS

Mood:

Premium / Spatial

Visual:

- Transparent surfaces
- Blur
- Layered depth
- Floating controls

Glass must not reduce readability.

Fallback gracefully on devices where blur is expensive.

---

# 19. THEME 12 — CARBON

Mood:

Industrial / Technical

Visual:

- Dark graphite
- Carbon-like texture
- Technical borders
- Dense information

---

# 20. THEME 13 — TOKYO NEON

Mood:

Cyberpunk / High Energy

Visual:

- Black
- Pink
- Cyan
- Neon accents

Motion:

- Fast
- Sharp
- Digital

Avoid excessive flashing.

---

# 21. THEME 14 — AMETHYST ROYAL

Mood:

Luxury / Premium

Visual:

- Deep purple
- Royal violet
- Dark surfaces

Motion:

- Smooth
- Elegant

---

# 22. THEME 15 — TITANIUM PRO

Mood:

Professional / Industrial

Visual:

- Metallic grey
- Graphite
- White

Motion:

- Precision
- Minimal

---

# 23. THEME 16 — SAKURA BLOSSOM

Mood:

Emotional / Artistic

Visual:

- Black
- Pink
- Soft highlights

Motion:

- Extremely subtle petal-inspired movement

Disable decorative particles under reduced-motion settings.

---

# 24. THEME 17 — DESERT DUNE

Mood:

Warm / Cinematic

Visual:

- Dark sand
- Amber
- Brown-black

Motion:

- Slow
- Cinematic

---

# 25. THEME 18 — HYPER LIME

Mood:

Extreme Energy

Visual:

- Black
- Electric lime

Motion:

- Fast
- Reactive

---

# 26. THEME 19 — DEEP OCEANIC

Mood:

Immersive / Deep

Visual:

- Almost-black navy
- Deep cyan

Motion:

- Slow
- Fluid

---

# 27. THEME 20 — CHRONO GOLD

Mood:

Luxury / Precision

Visual:

- Black
- Gold
- Graphite

Design inspiration:

- Precision instruments
- Mechanical watches
- Technical dashboards

Avoid looking like a generic luxury finance application.

---

# 28. LIGHT / DARK / SYSTEM

Every compatible theme must support:

- Light
- Dark
- System

System mode follows Android system preference.

Dark mode must not simply invert colors.

Each mode requires dedicated:

- Surface hierarchy
- Contrast
- Artwork overlay
- Text hierarchy
- Navigation treatment
- Player treatment

---

# 29. THEME TRANSITION

Changing themes must visually transform the application.

Transition:

1. User selects theme
2. Preview updates
3. User applies
4. Visual system transitions
5. Background changes
6. Surfaces transform
7. Typography treatment changes
8. Navigation changes
9. Player transforms
10. Widgets update

Use controlled motion.

Respect reduced-motion settings.

Theme changes must persist.

---

# 30. TYPOGRAPHY SYSTEM

NØTUNE supports at least 15 font options.

Font categories:

- Technical
- Minimal
- Geometric
- Editorial
- Monospace
- Futuristic
- Rounded

Fonts must be validated for:

- Latin
- Tamil
- Hindi
- Telugu
- Kannada
- Malayalam
- Bengali
- Marathi
- Gujarati
- Punjabi

If a selected font lacks required glyphs, use a compatible fallback.

Never display missing-glyph boxes.

---

# 31. TYPOGRAPHY ROLES

Independent font settings:

- App Font
- Heading Font
- Body Font
- Player Font
- Lyrics Font
- Numbers Font
- Technical/Data Font

Settings must support Android accessibility font scaling.

Never prevent system font scaling.

---

# 32. TEXT HIERARCHY

Required hierarchy:

Display
Heading
Title
Subtitle
Body
Caption
Label
Metadata
Technical

Music titles must remain visually dominant over secondary metadata.

---

# 33. ICONOGRAPHY

Icons must be:

- Consistent
- Recognizable
- Accessible
- Geometrically aligned

Icon styles may include:

- Outline
- Filled
- Technical
- Minimal
- Rounded
- Futuristic

Icon customization must remain semantically recognizable.

Do not replace standard system meanings with ambiguous decorative symbols.

---

# 34. LOGO SYSTEM

NØTUNE supports:

1. NØTUNE Wordmark
2. NØTUNE Dot
3. Cyber Orbit
4. Neon Ring
5. Hyper Cube
6. Wave
7. Spectrum
8. Pulse
9. Technical Grid
10. Eclipse
11. Signal
12. Infinity

Logo selection affects:

- App identity
- Splash
- Profile identity
- Widgets where appropriate
- Empty states
- AI identity

The logo must remain readable at small sizes.

---

# 35. SPACING SYSTEM

Use a centralized spacing scale.

Recommended base:

4dp

Common values:

4
8
12
16
20
24
32
40
48
64

Avoid arbitrary spacing unless required by platform conventions.

---

# 36. CORNER SYSTEM

NØTUNE supports multiple visual geometries.

Styles:

- Sharp
- Slightly rounded
- Rounded
- Fully rounded
- Editorial
- Technical

The selected style must affect:

- Cards
- Buttons
- Dialogs
- Inputs
- Sheets
- Player controls
- Social surfaces

---

# 37. ELEVATION

Prefer:

- Contrast
- Borders
- Layering
- Blur
- Artwork
- Subtle shadows

Avoid excessive Material-style floating cards.

Elevation should communicate hierarchy.

---

# 38. GLOBAL APP STRUCTURE

Primary navigation maximum:

5 destinations.

Required:

1. HOME
2. SEARCH
3. LIBRARY
4. SOCIAL
5. AI

Persistent:

MINI PLAYER

The mini player sits above primary navigation when music is playing.

---

# 39. HOME

Purpose:

Personalized music command center.

Sections may include:

- Greeting
- Continue Listening
- Recently Played
- Recommended
- New Releases
- Mood
- NØTUNE FLOW
- AI Picks
- Friends Listening
- Quick Actions
- Replay
- Music DNA
- Discovery Radar

Home must adapt to user behavior.

Do not fabricate recommendations.

If insufficient data exists, use clearly labeled generic discovery rather than pretending personalization exists.

---

# 40. HOME FUTURISTIC DASHBOARD

Home can include:

- Dynamic artwork
- Audio-reactive visual accents
- Music timeline
- Listening streak
- Current mood
- AI recommendation cards
- Friends activity
- Discovery radar

Decorative data must never be presented as real user analytics.

---

# 41. SEARCH

Search supports:

- Song
- Artist
- Album
- Playlist
- User
- Post
- Friend
- Lyrics
- Natural-language music queries
- Voice
- Song identification

Examples:

"Play relaxing Tamil songs"

"Find songs like this"

"Songs I listened to last week"

"Find energetic workout music"

AI interpretation must not invent results.

---

# 42. SEARCH RESULTS

Results should be grouped:

Songs
Artists
Albums
Playlists
People
Posts
Friends

Each result provides context actions.

Examples:

- Play
- Queue
- Save
- Favorite
- Share
- Add to playlist
- Suggest to friend

---

# 43. LIBRARY

Library includes:

- Songs
- Albums
- Artists
- Playlists
- Favorites
- Downloads
- Recently Played
- Recently Added
- Saved Social Music
- Folders

Support:

- List
- Grid
- Compact
- Artwork-heavy layouts

---

# 44. PLAYLISTS

Playlist design must expose:

- Artwork
- Title
- Description
- Creator
- Song count
- Duration
- Visibility
- Collaborative status
- Playlist DNA

Actions:

- Play
- Shuffle
- Download
- Share
- Edit
- Collaborate
- Start Listen Together

---

# 45. PLAYLIST DNA

Playlist DNA is calculated from actual playlist metadata and listening data.

Possible dimensions:

- Genres
- Artists
- Languages
- Mood
- Energy
- Era
- Discovery
- Repeat behavior

Never generate fake percentages.

If insufficient data:

"Not enough data yet."

---

# 46. FULL MUSIC PLAYER

The player is one of the primary NØTUNE experiences.

Required:

- Artwork
- Song title
- Artist
- Favorite
- Progress
- Duration
- Play/pause
- Previous
- Next
- Queue
- Shuffle
- Repeat
- Lyrics
- Output device
- Equalizer
- Download
- Share
- More

Optional contextual controls:

- AI
- Song identification
- Add to playlist
- Suggest to friend
- Listen Together

---

# 47. PLAYER STYLES

At least:

1. Classic
2. Minimal
3. Glass
4. Immersive
5. Waveform
6. Artwork Fullscreen
7. Technical
8. Compact
9. Cinema
10. Retro

Changing player style must affect the entire player layout.

---

# 48. PLAYER ARTWORK

Artwork modes:

- Standard
- Edge-to-edge
- Fullscreen
- Blurred background
- Dynamic color
- Monochrome
- Cinematic
- Technical

Artwork processing must avoid destroying text contrast.

---

# 49. PLAYER GESTURES

Supported gestures may include:

Swipe up:

Open queue

Swipe down:

Minimize

Swipe left:

Next

Swipe right:

Previous

Double tap:

Favorite

Long press:

Song actions

Two-finger swipe:

Volume

Shake:

Random/discovery action where permitted

All gestures must be configurable.

---

# 50. LYRICS

Lyrics screen supports:

- Original
- Translation
- Phonetic transliteration
- Romanization
- Sing-along
- Timed lyrics

Users must clearly understand whether text is:

- Original lyric
- Meaning translation
- Phonetic representation

---

# 51. SING-ALONG

Sing-along mode provides:

- Synchronized lyrics
- Current-line highlighting
- Upcoming lines
- Optional translation
- Optional phonetic text
- Font customization
- Text size
- Motion settings

---

# 52. NØTUNE FLOW

NØTUNE FLOW is the dynamic mood-radio experience.

Inputs may include:

- Current song
- Listening history
- Mood
- Energy
- Genre
- Language
- Time
- User interaction

Controls:

- More like this
- Less like this
- Change mood
- Increase energy
- Decrease energy
- Explore
- Save

---

# 53. MUSIC DNA

Music DNA visualizes actual listening behavior.

Possible dimensions:

- Top artists
- Genres
- Languages
- Eras
- Moods
- Energy
- Listening time
- Discovery rate
- Repeat rate
- Artist network
- Genre constellation
- Timeline
- Heatmap

Periods:

- This Week
- This Month
- Last Month
- This Year
- All Time

Never show random statistics.

---

# 54. REPLAY

Replay provides real listening summaries.

Possible sections:

- Top songs
- Top artists
- Top albums
- Top genres
- Listening time
- Discovery
- Repeat
- Monthly timeline
- Yearly story

Insufficient data must be communicated clearly.

---

# 55. SOCIAL

Social is a music-first social network.

Main sections:

- Feed
- Friends
- Messages
- Stories
- Rooms
- Circles
- Compatibility
- Challenges
- Polls
- Notifications

---

# 56. SOCIAL FEED

Posts may contain:

- Song
- Album
- Artist
- Playlist
- Lyrics
- Song Moment
- Mood
- Poll
- Listening Activity

Actions:

- Like
- React
- Comment
- Share
- Repost
- Save
- Play

Music should remain the visual center of the post.

---

# 57. MUSIC STORIES

Stories expire after 24 hours.

Story types:

- Song
- Playlist
- Album
- Mood
- Listening Activity
- Song Moment
- Text + Music

Interaction:

- React
- Reply
- Play
- Share

Privacy settings must be respected.

---

# 58. SONG MOMENT

Users can share an exact moment of a song.

Example:

02:14 — THIS PART 🔥

The shared moment should contain:

- Song
- Artist
- Timestamp
- Optional note

Tapping the moment opens playback at the relevant timestamp where technically possible.

---

# 59. REACTIONS

Music reactions:

- Love
- Fire
- Feels
- Mind Blown
- Hurt
- Vibe

Reaction animation should be short and lightweight.

---

# 60. FRIENDS

Friends page supports:

- Search
- Requests
- Suggestions
- Online
- Listening
- Listening Together
- Away
- Offline
- Invisible

Actions:

- Add
- Accept
- Reject
- Cancel
- Remove
- Block
- Report

---

# 61. FRIEND PROFILE

Profile includes:

- Avatar
- Username
- Bio
- Music DNA
- Favorite artists
- Public playlists
- Posts
- Friends
- Compatibility

Privacy rules must control visibility.

---

# 62. MUSIC COMPATIBILITY

Compatibility must use actual shared music signals.

Potential inputs:

- Liked songs
- Artists
- Genres
- Languages
- Playlists
- Listening history
- Music DNA

Never fabricate compatibility.

If insufficient data:

"Not enough shared listening data."

---

# 63. MESSAGING

Messages support:

- Text
- Song
- Album
- Artist
- Playlist
- Lyrics
- Song Moment
- Song Suggestion
- Listening Invite
- Room Invite
- Music Gift

Message states:

- Sending
- Sent
- Delivered
- Read
- Failed

Offline messages use an outbox and retry system.

---

# 64. SONG SUGGESTIONS

A song suggestion contains:

- Sender
- Song
- Optional note
- Playback action
- Save
- Add to playlist
- Reply
- Like
- Dismiss

Song suggestions should look like music objects rather than generic chat attachments.

---

# 65. LISTEN TOGETHER

Listen Together is a synchronized music experience.

Required:

- Shared playback
- Queue
- Host
- Members
- Reactions
- Chat
- Invitations
- Playback synchronization

Playback state must be authoritative and synchronized.

Never fake synchronization in production.

---

# 66. ROOMS

Room types:

- Public
- Friends
- Private
- Couple

Room UI includes:

- Room identity
- Current song
- Members
- Queue
- Host controls
- Chat
- Reactions
- Invite
- Leave
- Report

---

# 67. ROOM DISCOVERY

Public rooms may appear in discovery.

Display only verified/actual values.

Examples:

- Current song
- Member count
- Room type
- Host
- Activity

Never fabricate live member counts.

---

# 68. COUPLE MODE

Couple Mode is a dedicated two-person music experience.

Features:

- Our Song
- Couple DNA
- Couple Radio
- Couple AI DJ
- Song Battle
- Guess My Song
- Secret Dedication
- Memory Capsules
- Relationship Timeline
- Distance Mode

Visual language:

- Intimate
- Cinematic
- Personal
- Music-focused

Avoid generic dating-app visual patterns.

---

# 69. FRIEND CIRCLES

Friend Circles provide group music spaces.

Features:

- Group chat
- Shared playlist
- Shared queue
- Polls
- Challenges
- Listen Together

---

# 70. MUSIC POLLS

Polls can ask:

"Which song wins?"

"Which artist?"

"Which version?"

Polls show real votes only.

After voting:

- Selected state
- Current results
- Total votes

Never invent engagement.

---

# 71. MUSIC CHALLENGES

Examples:

- 30-day discovery challenge
- Genre challenge
- Artist challenge
- Listening streak
- Friend challenge

Progress must come from actual user activity.

---

# 72. NOTIFICATIONS

Notifications cover:

- Friend requests
- Messages
- Song suggestions
- Room invites
- Playlist invites
- Playlist updates
- Likes
- Comments
- Mentions
- Reposts
- Story reactions
- Listening activity

Notifications should deep-link to the exact relevant object.

---

# 73. PROFILE

User profile includes:

- Avatar
- Username
- Bio
- Music DNA
- Favorite artists
- Playlists
- Posts
- Listening identity
- Social connections
- Customization identity

Privacy controls must be visible.

---

# 74. AI

AI is a first-class NØTUNE system.

Main AI entry:

NØTUNE AI

Modes:

- Ask NØTUNE
- AI DJ
- AI Music Finder
- AI Playlist Generator
- AI Radio
- AI Suggester
- Song Explainer
- Lyrical Insights
- Music DNA analysis
- Voice Control
- AI Playground

---

# 75. AI INTERACTION DESIGN

AI responses must be:

- Concise by default
- Contextual
- Actionable
- Music-aware

Whenever possible, AI should produce actions.

Example:

User:

"Play relaxing Tamil songs."

AI:

Finds actual matching songs and starts playback.

AI must never claim to have completed an action unless the action actually succeeded.

---

# 76. AI ACTION SYSTEM

AI actions must use controlled application tools.

Examples:

- Search
- Play
- Pause
- Next
- Favorite
- Add to playlist
- Create playlist
- Start room
- Invite friend
- Enable sleep timer
- Enable private session

AI must not have unrestricted access to sensitive account/security operations.

---

# 77. AI PROVIDER ARCHITECTURE

Supported providers may include:

- Gemini
- Groq
- OpenRouter
- Ollama
- Local NØTUNE Intelligence

User experience:

AI ON/OFF

Do not expose complex provider/API-key configuration in normal consumer UI.

If AI is unavailable:

Use truthful local capabilities.

Never call deterministic fallback behavior an LLM.

---

# 78. AI PLAYGROUND

AI Playground is an experimentation area.

Possible tools:

- Music prompts
- Mood generation
- Playlist concepts
- Song analysis
- Music conversations
- AI experiments

Experimental features must be clearly labeled.

---

# 79. DOWNLOADS

Downloads page:

- Active
- Completed
- Paused
- Failed
- Cancelled

Actions:

- Pause
- Resume
- Cancel
- Retry
- Delete

Show:

- Progress
- Size
- Speed
- Remaining
- Status

Do not display fake progress.

---

# 80. STORAGE MANAGER

Show actual:

- Music storage
- Downloads
- Artwork cache
- Social cache
- AI cache
- Other app data

Actions must affect actual storage.

---

# 81. EQUALIZER

Equalizer UI should support:

- Presets
- Bands
- Gain
- Enable/disable
- Reset

If Android/device limitations prevent a feature from working, explain the limitation.

Do not show controls that do nothing.

---

# 82. SONG IDENTIFICATION

Song identification UI should clearly communicate:

- Listening
- Identifying
- Match found
- No match
- Permission denied
- Network unavailable

Results should provide:

- Song
- Artist
- Album
- Play
- Save
- Share

---

# 83. RINGTONE / AUDIO CLIP

Use Android-supported mechanisms.

Provide:

- Selection
- Preview
- Trim where supported
- Set action
- Success
- Failure

Never pretend the ringtone was changed if Android rejected the operation.

---

# 84. SETTINGS

Settings categories:

Playback
Audio
Downloads
Appearance
Typography
Player
Navigation
Cards
Gestures
Widgets
AI
Social
Privacy
Account
Notifications
Accessibility
Storage
About

---

# 85. NØTUNE CUSTOM LAB

Custom Lab is the central customization system.

Sections:

- Themes
- Fonts
- Logos
- Player
- Navigation
- Cards
- Gestures
- Widgets
- Motion

Use live preview.

Actions:

APPLY
CANCEL
RESET

Changes should not be partially applied.

---

# 86. LIVE PREVIEW

Customization previews must show realistic UI.

Preview examples:

- Home
- Player
- Social
- AI
- Settings
- Widget

Preview data may be static demonstration content.

It must be visually obvious that preview data is not real account data.

---

# 87. NAVIGATION CUSTOMIZATION

Navigation styles:

- Standard
- Floating
- Compact
- Gesture

Options:

- Icon size
- Label visibility
- Spacing
- Selected indicator

Navigation must remain accessible.

---

# 88. CARD CUSTOMIZATION

Card styles:

- Flat
- Glass
- Technical
- Rounded
- Sharp
- Editorial
- Compact
- Artwork

The selected style should apply consistently across the application.

---

# 89. GESTURE CENTER

Every configurable gesture must show:

- Gesture
- Current action
- Enabled state
- Conflict state

Provide conflict detection.

Example:

Two gestures cannot both claim the same gesture without explicit resolution.

---

# 90. WIDGETS

Minimum widgets:

1. Now Playing
2. Compact Player
3. Lyrics
4. Playlist
5. Quick Controls
6. Music DNA
7. Friends Listening
8. NØTUNE FLOW

---

# 91. WIDGET STYLES

Styles:

- Minimal
- Glass
- Technical
- Artwork
- Waveform
- Compact
- Editorial
- Pixel
- Cinema
- Dynamic

Widgets follow selected theme where technically supported.

---

# 92. ONBOARDING

Onboarding must be short and progressive.

Suggested sequence:

1. NØTUNE identity
2. Music permission
3. Notification permission where necessary
4. Choose listening preferences
5. Choose theme
6. Choose font
7. Choose logo
8. Explain mini player
9. Explain AI
10. Explain Social
11. Explain Music DNA
12. Finish

Do not request every optional permission immediately.

---

# 93. FIRST-RUN EXPERIENCE

First launch should prioritize:

GET MUSIC PLAYING.

Do not force users through dozens of configuration screens.

If the library is empty:

Provide:

- Add music
- Scan library
- Discover music
- Learn NØTUNE

---

# 94. INTERACTIVE TUTORIAL

Tutorials should be contextual.

Examples:

First opening player:

"Swipe up for queue."

First social visit:

"Share what you're listening to."

First AI visit:

"Ask NØTUNE to find or play music."

First Listen Together visit:

"Listen in sync with friends."

Tutorials can be dismissed and should not repeatedly interrupt experienced users.

---

# 95. EMPTY STATES

Every major page must have meaningful empty states.

Examples:

No songs:

"Your library is empty."

No friends:

"Your music circle starts here."

No messages:

"Send someone a song."

No Music DNA:

"Keep listening to build your Music DNA."

No Replay:

"Listen more to unlock Replay."

Empty states should provide an action.

---

# 96. LOADING STATES

Loading should use:

- Skeletons
- Progress indicators
- Shimmer only where useful
- Artwork placeholders

Avoid excessive animation.

---

# 97. ERROR STATES

Errors must explain:

- What failed
- Whether data was saved
- Whether retry may work
- What the user can do

Actions:

RETRY
CANCEL
GO BACK
WORK OFFLINE where applicable

Never show fake successful states.

---

# 98. OFFLINE STATES

Offline mode must be first-class.

Show:

- Offline indicator
- Cached content
- Downloaded music
- Pending actions

Queue actions where possible.

Never imply cloud synchronization completed while offline.

---

# 99. PERMISSION STATES

Permission denied UI must explain:

- Why permission is needed
- What functionality is affected
- How to grant it

Do not repeatedly request denied permissions without context.

---

# 100. SOCIAL PRIVACY STATES

Every social object must respect:

- Public
- Friends
- Private
- Blocked
- Restricted

Private data must never leak into:

- Search
- Feed
- Suggestions
- Compatibility
- Discovery
- Notifications

---

# 101. PRIVACY CENTER

Privacy Center should expose understandable controls for:

- Profile visibility
- Listening activity
- Friend discovery
- Social posts
- Stories
- Messages
- Room visibility
- AI data
- Analytics
- Data export
- Data deletion
- Private Session

Privacy explanations must be truthful and specific.

---

# 102. PRIVATE SESSION

Private Session visually indicates:

PRIVATE SESSION ACTIVE

It must prevent configured private listening activity from being written to normal history/social surfaces.

All relevant data writers must enforce the boundary.

UI alone is insufficient.

---

# 103. ACCESSIBILITY

NØTUNE must support:

- Screen readers
- Content descriptions
- Large text
- Font scaling
- High contrast
- Reduced motion
- Touch target minimums
- Keyboard navigation where applicable
- Color-independent status
- Accessible focus indicators

Never communicate status using color alone.

---

# 104. REDUCED MOTION

When reduced motion is enabled:

Disable or reduce:

- Particles
- Continuous gradients
- Parallax
- Excessive transitions
- Shaking
- Decorative animations

Keep essential state transitions understandable.

---

# 105. MOTION SYSTEM

Motion should communicate:

- Navigation
- Hierarchy
- State
- Cause/effect
- Playback

Motion categories:

Micro:
100–180ms

Standard:
180–300ms

Expressive:
300–500ms

Long cinematic transitions should be used sparingly.

---

# 106. MICRO-INTERACTIONS

Examples:

Favorite:

Small pulse

Play:

Control transforms

Download:

Progress indicator

Theme:

Global visual transformation

Friend request:

State transition

Song suggestion:

Card confirmation

Room join:

Transition into synchronized player

AI action:

Action confirmation

Micro-interactions must never block normal interaction.

---

# 107. AUDIO-REACTIVE DESIGN

Optional audio-reactive visuals may respond to:

- Beat
- Volume
- Energy
- Waveform

Audio-reactive effects must be:

- Lightweight
- Battery-conscious
- Optional
- Reduced under accessibility settings

Do not claim audio-reactive synchronization if the implementation is not actually synchronized.

---

# 108. SOCIAL PRESENCE DESIGN

Presence indicators:

Online
Listening
Listening Together
Away
Offline
Invisible

Use:

- Icon
- Label
- Accessible description

Color alone must not represent presence.

---

# 109. REAL-TIME DESIGN

Real-time UI must distinguish:

Connected
Connecting
Reconnecting
Offline
Failed

For Listen Together:

Synchronizing
Synchronized
Drifting
Resynchronizing

Do not hide connection problems.

---

# 110. DATA TRUST PRINCIPLE

NØTUNE must follow:

REAL DATA > PRETTY FAKE DATA

Never fabricate:

- Listening statistics
- Friend counts
- Likes
- Comments
- Views
- Compatibility
- Room members
- Trending numbers
- AI execution
- Download progress
- Synchronization state
- Playback state

If information is unavailable:

Say so.

---

# 111. RESPONSIVE DESIGN

Layouts must adapt to:

- Small phones
- Large phones
- Foldables
- Tablets
- Landscape
- Split-screen where supported

Do not simply stretch phone layouts.

---

# 112. TOUCH TARGETS

Interactive elements must provide comfortable touch targets.

Small visual icons may exist inside larger touch targets.

Never require precision tapping.

---

# 113. SHEETS AND DIALOGS

Use bottom sheets for:

- Song actions
- Queue actions
- Playlist actions
- Share
- Friend actions

Use dialogs for:

- Confirmation
- Destructive actions
- Critical warnings

Avoid nested modal chains.

---

# 114. CONTEXT MENUS

Long-press and overflow actions should expose:

- Play
- Queue
- Favorite
- Playlist
- Share
- Download
- Suggest
- Identify
- Details

Menu contents must depend on actual object/state.

---

# 115. GLOBAL QUICK ACTIONS

A command palette / quick action interface can provide:

- Search
- Play
- Queue
- Favorite
- Open player
- Open lyrics
- Create playlist
- Start room
- Ask AI
- Open settings

Actions must execute actual functionality.

---

# 116. SHARE SYSTEM

Shareable objects:

- Song
- Album
- Artist
- Playlist
- Post
- Profile
- Story
- Room
- Song Moment

Use Android sharing mechanisms where appropriate.

Deep links must resolve to actual objects.

---

# 117. DEEP LINKS

Support deep links for:

- Songs
- Albums
- Artists
- Playlists
- Posts
- Profiles
- Stories
- Rooms
- Messages
- Listen Together

Invalid links must produce a clear error state.

---

# 118. MUSIC-FIRST SOCIAL COMPONENTS

Reusable components should include:

MusicCard
SongCard
AlbumCard
ArtistCard
PlaylistCard
SongMomentCard
MusicPostCard
SongSuggestionCard
ListeningActivityCard
RoomCard
FriendCard
MusicDNACard

Components should share a consistent design language.

---

# 119. AI COMPONENTS

Reusable components:

AIMessage
AIAction
AIRecommendation
AIPlaylist
AIExplanation
AICommand
AIStatus
AIError

AI-generated content must be distinguishable from user-generated content.

---

# 120. PLAYER COMPONENTS

Reusable components:

MiniPlayer
FullPlayer
Queue
ProgressBar
Waveform
PlaybackControls
LyricsPreview
OutputSelector
EqualizerControl
DownloadControl

---

# 121. SOCIAL COMPONENTS

Reusable:

ReactionBar
CommentThread
StoryRing
PresenceBadge
FriendRequestCard
MessageBubble
SongMessage
RoomInvite
NotificationItem
CompatibilityCard

---

# 122. SECURITY-RELATED UI

Security-sensitive screens should visually communicate trust without making unsupported claims.

Examples:

- Private Session
- Encrypted conversation
- Room privacy
- Account security
- Data export
- Data deletion

Never label a communication "end-to-end encrypted" unless the complete E2E protocol is actually implemented and verified.

Cryptographic primitives alone are not sufficient.

---

# 123. PROFILE CUSTOMIZATION

Users may customize:

- Avatar
- Logo
- Theme identity
- Bio
- Favorite music
- Public playlists
- Profile layout where supported

Avoid allowing customization to destroy accessibility or readability.

---

# 124. MUSIC IDENTITY

Music identity should feel like a personal operating profile.

Possible visualization:

- Music DNA
- Favorite artists
- Favorite genres
- Languages
- Listening patterns
- Discovery radar
- Listening timeline

The visual should feel analytical but emotional.

---

# 125. DISCOVERY RADAR

Discovery Radar visualizes actual discovery opportunities.

Potential dimensions:

- New artist
- New genre
- New language
- New era
- Similar music

Only show meaningful dimensions with sufficient data.

---

# 126. MUSIC MEMORY

Music Memory can surface actual historical listening moments.

Examples:

"You listened to this song 3 years ago."

"This playlist became active during this period."

Historical statements must come from stored data.

---

# 127. YEARLY MUSIC STORY

Yearly story can summarize:

- Top songs
- Top artists
- Genres
- Languages
- Listening time
- Discovery
- Memories

Every metric must be calculated from actual data.

---

# 128. MODE SYSTEM

NØTUNE can provide behavioral modes:

- Car Mode
- Travel Mode
- Focus Mode
- Sleep Mode
- Party Mode
- Private Session

Modes may alter:

- UI density
- Playback behavior
- Notifications
- AI behavior
- Visual motion
- Controls

Modes must clearly indicate when active.

---

# 129. CAR MODE

Prioritize:

- Large controls
- Minimal text
- Voice control
- Previous
- Play
- Next
- Favorite

Reduce distracting information.

---

# 130. FOCUS MODE

Focus Mode may prioritize:

- Instrumental music
- Low-distraction UI
- Minimal notifications
- Focus playlists

Only use actual available music data.

---

# 131. SLEEP MODE

Sleep Mode may provide:

- Sleep timer
- Ambient music
- Reduced motion
- Dim visual design

Timer state must be real.

---

# 132. PARTY MODE

Party Mode may emphasize:

- Shared queue
- Voting
- Room
- Reactions
- High-energy visuals

Do not fabricate party activity.

---

# 133. COMMAND PALETTE

The command palette should feel like a system-level control layer.

Examples:

"Play"
"Search"
"Create playlist"
"Start room"
"Ask NØTUNE"
"Open Music DNA"
"Enable Private Session"

Search results must expose actual available commands.

---

# 134. ANIMATION PERFORMANCE

Animations must:

- Use GPU-friendly operations
- Avoid unnecessary recomposition
- Avoid continuous expensive rendering
- Respect battery
- Respect reduced motion

Never prioritize animation over playback reliability.

---

# 135. BACKGROUND PLAYBACK UI

Background playback should integrate with:

- Notification
- Lock screen
- Bluetooth controls
- Android media controls

The visual identity should remain consistent where platform APIs permit customization.

---

# 136. NOTIFICATION DESIGN

Playback notification:

- Artwork
- Song
- Artist
- Playback controls

Social notifications:

- Relevant music context
- Sender
- Action

Avoid generic notifications without context.

---

# 137. ERROR COPY STYLE

Error messages should be:

- Human
- Short
- Specific
- Actionable

Bad:

"Error 500."

Better:

"Couldn't load your friends. Check your connection and try again."

Technical details may be available under advanced diagnostics.

---

# 138. EMPTY COPY STYLE

Empty-state copy should feel like NØTUNE.

Examples:

"Nothing here yet."

"Your next favorite song is waiting."

"Keep listening to build your Music DNA."

"Your music circle starts here."

Avoid childish or excessive copy.

---

# 139. SUCCESS COPY

Success states should be subtle.

Examples:

"Added to playlist."

"Saved."

"Room created."

"Friend request sent."

Do not interrupt the music experience unnecessarily.

---

# 140. DESTRUCTIVE ACTIONS

Examples:

Delete playlist
Remove friend
Leave room
Delete post
Delete message
Clear history
Delete downloads

Require appropriate confirmation.

Never hide destructive actions behind unclear labels.

---

# 141. OFFLINE-FIRST VISUAL LANGUAGE

Offline should not make the app feel broken.

Show:

OFFLINE

Then provide available local functionality.

Examples:

- Local music
- Downloads
- Cached playlists
- Cached Music DNA
- Pending social actions

---

# 142. SYNCHRONIZATION DESIGN

Cloud sync states:

Synced
Syncing
Pending
Conflict
Failed
Offline

Never show "Synced" without successful synchronization.

---

# 143. CONFLICT DESIGN

For collaborative playlists or multi-device changes:

- Detect conflict
- Preserve user changes where possible
- Explain conflict
- Resolve deterministically
- Avoid silent data loss

UI should expose meaningful resolution when user intervention is required.

---

# 144. COMPONENT STATES

Every interactive component should support as applicable:

- Default
- Hover
- Pressed
- Focused
- Selected
- Disabled
- Loading
- Error
- Success

Do not rely solely on color.

---

# 145. PAGE STATES

Every major page must support:

- First use
- Loading
- Loaded
- Empty
- No results
- Offline
- Error
- Retry
- Permission denied
- Partial data

---

# 146. SOCIAL MODERATION UI

Provide:

- Report
- Block
- Mute
- Restrict
- Delete own content

Report UI should provide meaningful categories.

Do not expose moderation controls where the user lacks permission.

---

# 147. CONTENT VISIBILITY

Before rendering any social content, enforce:

- Ownership
- Visibility
- Friendship relationship
- Block status
- Room membership
- Permission

UI filtering is not a substitute for backend authorization.

---

# 148. REAL-TIME ROOM DESIGN

Room events may include:

- Member joined
- Member left
- Host changed
- Queue updated
- Playback changed
- Reaction
- Chat message
- Invite

Events should animate subtly.

Do not animate every event aggressively.

---

# 149. LISTEN TOGETHER SYNC VISUALIZATION

Show a subtle synchronization indicator.

States:

SYNCED
SYNCING
DRIFT
RESYNC

The indicator should be based on actual synchronization measurements.

---

# 150. MUSIC PLAYBACK STATES

Playback states:

- Idle
- Loading
- Playing
- Paused
- Buffering
- Seeking
- Completed
- Failed
- Offline

UI must represent the actual Media3/player state.

---

# 151. ACCESSIBILITY LABELS

Examples:

Play:

"Play song"

Pause:

"Pause song"

Favorite:

"Add song to favorites"

If already favorite:

"Remove song from favorites"

Queue:

"Open queue"

Accessibility labels must represent current state.

---

# 152. COLOR CONTRAST

All text and interactive states must maintain sufficient contrast.

Theme customization cannot be allowed to create unreadable combinations without an accessible fallback.

---

# 153. FONT FALLBACK

When a selected font lacks glyphs:

1. Preserve selected font for supported glyphs
2. Automatically use compatible fallback
3. Maintain visual consistency
4. Never show missing glyphs

---

# 154. ARTWORK FALLBACK

If artwork is unavailable:

Use a generated visual based on actual metadata where possible.

Fallback must not imply official artwork.

Possible fallback:

- NØTUNE logo
- Gradient
- Monogram
- Metadata-derived pattern

---

# 155. AVATAR FALLBACK

If no avatar:

Use:

- Initial
- Generated abstract identity
- NØTUNE profile logo

Do not download random profile images.

---

# 156. NETWORK STATE

Global network state may be represented through a subtle status indicator.

States:

Online
Limited
Offline
Reconnecting

Avoid persistent distracting banners.

---

# 157. PRIVACY VISUAL LANGUAGE

Privacy-related UI should use:

- Clear labels
- Plain language
- Minimal fear-based imagery
- Explicit state

Avoid fake "military-grade" or unsupported security marketing.

---

# 158. AI PRIVACY

AI UI should explain when applicable:

- Local processing
- Cloud processing
- Data sent to provider
- AI disabled

Do not claim local processing if a cloud provider is being used.

---

# 159. DATA EXPORT

Export UI should clearly explain:

- What is included
- Format
- Whether encryption is used
- Where data will go

If export is unencrypted, say so before sharing.

---

# 160. RESET EXPERIENCE

Reset should distinguish:

Local reset
Cloud data deletion
Downloads
Cached files
Account deletion

Do not imply that deleting local data automatically deletes server-side data.

---

# 161. DESIGN SYSTEM ARCHITECTURE

Recommended conceptual hierarchy:

NØTUNE Design Tokens
        ↓
Theme Engine
        ↓
Typography Engine
        ↓
Component System
        ↓
Page Templates
        ↓
Feature Screens
        ↓
Modes / Customization

No page should independently reinvent the design system.

---

# 162. COMPOSABLE UI

Prefer reusable components.

Examples:

MusicCard
ActionButton
SectionHeader
ArtworkSurface
PlayerControl
SocialCard
AIAction
PresenceBadge
StatusIndicator

Avoid duplicated visual implementations.

---

# 163. DESIGN CONSISTENCY

If a component appears in:

Home
Search
Library
Social
AI

it should retain recognizable behavior and styling unless the selected context intentionally changes it.

---

# 164. CUSTOMIZATION BOUNDARIES

Customization may change:

- Visual appearance
- Layout style
- Typography
- Animation
- Icons
- Player
- Navigation
- Cards

Customization must NOT break:

- Accessibility
- Playback
- Navigation
- Privacy
- Security
- Data integrity
- Android conventions

---

# 165. THEME PERFORMANCE

Themes must not introduce:

- Excessive memory usage
- Continuous CPU load
- Battery drain
- Playback interruption

GPU-heavy effects must have lightweight fallbacks.

---

# 166. DESIGN QA

Every screen must be tested in:

- Default theme
- Light mode
- Dark mode
- Large text
- Reduced motion
- Offline
- Loading
- Error
- Empty
- Permission denied

---

# 167. DEVICE QA

Validate:

- Small Android phone
- Large Android phone
- High refresh rate
- Low-end device
- Tablet
- Foldable where applicable
- Landscape

---

# 168. PLAYBACK-FIRST PRINCIPLE

No design effect may interrupt:

- Playback
- Background playback
- Queue
- Bluetooth control
- Audio focus
- Download reliability

Music functionality has priority over visual effects.

---

# 169. SOCIAL-FIRST PRINCIPLE

Social UI must always preserve music context.

A song shared in:

- Feed
- Story
- Message
- Room
- Playlist
- Profile

must remain recognizable as a music object.

---

# 170. AI-FIRST PRINCIPLE

AI should be available from context.

Examples:

Player → Ask AI about song

Search → Ask AI to refine search

Playlist → Ask AI to transform playlist

Music DNA → Ask AI to explain taste

Social → Ask AI for music suggestions

AI must remain optional.

---

# 171. NO FAKE IMPLEMENTATION

This is a mandatory design/implementation rule.

Do not design interactions that imply functionality which does not exist.

Examples:

Do not show:

"Live synchronized"

unless synchronization is actually implemented.

Do not show:

"Encrypted"

unless encryption is actually implemented.

Do not show:

"AI generated"

unless AI actually generated it.

Do not show:

"Downloaded"

unless download completed.

Do not show:

"Online"

unless presence is actually verified.

---

# 172. PRODUCTION PRINCIPLE

The visual design must be backed by real application behavior.

Every important visual state should correspond to an actual application state.

UI is not allowed to simulate backend success.

---

# 173. DESIGN ACCEPTANCE

A screen is design-complete only when:

- Layout is consistent
- Typography is correct
- Theme works
- Dark mode works
- Accessibility works
- Loading exists
- Empty exists
- Error exists
- Offline exists where applicable
- Permission state exists where applicable
- Motion is appropriate
- Touch targets are usable
- Data states are truthful
- Navigation is clear

---

# 174. FUTURISTIC DESIGN RULE

NØTUNE should feel like a product from the future without looking like a science-fiction concept demo.

Futuristic elements should come from:

- Precision
- Motion
- Context
- Intelligent information
- Adaptive surfaces
- Music-reactive UI
- Personalization
- Spatial hierarchy
- Real-time interaction

Not from excessive decoration.

---

# 175. PREMIUM DESIGN RULE

Premium does not mean:

- More gradients
- More shadows
- More animations
- More glass
- More colors

Premium means:

- Better hierarchy
- Better spacing
- Better typography
- Better motion
- Better consistency
- Better responsiveness
- Better interaction feedback
- Better accessibility
- Better performance

---

# 176. EMOTIONAL DESIGN

Music is emotional.

NØTUNE should allow visual intensity to adapt to the music context.

Examples:

Calm song:

Soft motion

High-energy song:

Stronger reactive UI

Sad song:

Muted cinematic presentation

Party:

More dynamic controls

Sleep:

Minimal low-stimulation UI

These changes must remain subtle and user-controllable.

---

# 177. HOME → PLAYER TRANSITION

When opening the full player:

- Artwork expands
- Background transforms
- Mini player becomes full player
- Content hierarchy changes
- Controls appear progressively

The transition should feel like entering the music.

---

# 178. PLAYER → LYRICS TRANSITION

Lyrics should feel connected to the current song.

Artwork may remain as:

- Background
- Header
- Small identity element

The song identity should never disappear.

---

# 179. PLAYER → AI TRANSITION

AI context should automatically know the currently playing song when permitted.

Examples:

"Explain this song."

"Translate these lyrics."

"Find songs like this."

"Who is the artist?"

AI should preserve current playback context.

---

# 180. SOCIAL → PLAYER TRANSITION

When playing a song from a social post:

- Preserve source context where possible
- Show originating post
- Keep playback persistent

---

# 181. MESSAGE → PLAYER

Song messages should allow:

- Preview
- Play
- Save
- Add to playlist
- Open full player

Do not require unnecessary navigation.

---

# 182. ROOM → PLAYER

Room playback should visually distinguish:

Local playback
Room playback

Show synchronization status.

---

# 183. PROFILE → MUSIC DNA

Music DNA should feel like the identity layer of the profile.

Use visualizations such as:

- Constellation
- Radar
- Timeline
- Network
- Heatmap

Only use visualizations supported by real data.

---

# 184. MUSIC VISUALIZATION RULE

Charts and visualizations must represent real data.

Never use random values merely to make the UI look populated.

---

# 185. SOCIAL DISCOVERY

Discovery may include:

- People
- Playlists
- Posts
- Friends listening
- Public rooms

Rankings must be based on real signals.

Avoid fake "trending" numbers.

---

# 186. SEARCH DISCOVERY

Search can combine:

Exact search
Semantic search
AI interpretation
Music metadata

Results must clearly distinguish:

Exact result
Suggested result
AI interpretation

---

# 187. AI LOADING

AI loading should feel intelligent but restrained.

Example:

NØTUNE THINKING

Possible subtle animation:

Three technical indicators.

Do not use long decorative animations.

---

# 188. AI ERROR

Example:

"NØTUNE couldn't complete that request."

Actions:

TRY AGAIN
USE LOCAL FEATURES

If the issue is network/provider related, explain that appropriately.

---

# 189. SOCIAL LOADING

Use skeleton cards.

Avoid showing fake usernames, posts or counts while loading.

---

# 190. REAL DATA PLACEHOLDERS

Loading placeholders may use abstract geometry.

Do not use realistic fake social content that could be mistaken for real data.

---

# 191. DEMO / PREVIEW MODE

If a design preview contains demonstration data:

Clearly label:

PREVIEW

Preview data must never enter real user storage.

---

# 192. DESIGN DOCUMENT RULE

This DESIGN.md defines:

Visual language
Interaction language
Component behavior
Motion
Themes
Typography
Accessibility
Page-level design

It does not replace:

Security architecture
Backend architecture
Database schema
API contracts
Legal/licensing requirements
Testing strategy

Those systems must remain separately documented.

---

# 193. ENGINEERING HANDOFF

Developers implementing this design should:

- Use centralized design tokens
- Use reusable components
- Avoid screen-specific styling duplication
- Avoid hardcoded colors
- Avoid arbitrary dimensions
- Support dynamic theme changes
- Support font fallback
- Support accessibility
- Support reduced motion
- Preserve actual state
- Avoid fake data
- Avoid fake loading completion
- Avoid fake backend states

---

# 194. DESIGN IMPLEMENTATION PRIORITY

Implement visual slices in this order:

1. App shell
2. Theme engine
3. Typography
4. Components
5. Home
6. Search
7. Library
8. Mini player
9. Full player
10. Lyrics
11. Playlist
12. Music DNA
13. Social
14. Friends
15. Messages
16. Listen Together
17. Rooms
18. Couple
19. AI
20. Custom Lab
21. Widgets
22. Settings
23. Accessibility
24. Error/offline states

---

# 195. VERTICAL SLICE PRINCIPLE

Do not build hundreds of disconnected screens.

Build complete user journeys.

Primary journey:

Song
→ Play
→ Favorite
→ Playlist
→ Share
→ Message
→ Post
→ Friend
→ Listen Together
→ Music DNA

Social journey:

Profile
→ Friend
→ Presence
→ Message
→ Song Suggestion
→ Playlist
→ Room

Room journey:

Create Room
→ Invite
→ Join
→ Queue
→ Sync
→ Chat
→ Reaction
→ Leave

AI journey:

Ask
→ Search
→ Play
→ Modify Queue
→ Create Playlist
→ Share

---

# 196. FINAL NØTUNE DESIGN STANDARD

NØTUNE should feel like:

A music player
that became an operating system
that became a social network
that gained intelligence
without losing the music.

The user should always understand:

WHAT IS PLAYING
WHAT CAN I DO
WHAT IS HAPPENING
WHO AM I LISTENING WITH
WHAT DOES NØTUNE KNOW
WHAT DATA IS REAL
WHAT IS PRIVATE
WHAT CAN I CONTROL

The interface must be:

FAST
CLEAR
MUSICAL
INTELLIGENT
SOCIAL
PERSONAL
FUTURISTIC
ACCESSIBLE
PRIVATE
TRUTHFUL

The ultimate design principle:

MUSIC IS THE CORE.
AI IS THE INTELLIGENCE.
SOCIAL IS THE CONNECTION.
PERSONALIZATION IS THE IDENTITY.
PRIVACY IS THE TRUST.
NØTUNE IS THE SYSTEM.
