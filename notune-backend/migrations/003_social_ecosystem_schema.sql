-- ====================================================================
-- NØTUNE Social Ecosystem PostgreSQL Schema Migration (003)
-- Full persistence for multi-user social network, presence, DMs, posts,
-- reactions, comments, collaborative playlists, circles, stories & polls.
-- ====================================================================

-- 1. Friendships & Block List
CREATE TABLE IF NOT EXISTS friendships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requester_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    addressee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED, BLOCKED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_friendship_pair UNIQUE (requester_id, addressee_id)
);

CREATE INDEX IF NOT EXISTS idx_friendships_requester ON friendships(requester_id);
CREATE INDEX IF NOT EXISTS idx_friendships_addressee ON friendships(addressee_id);
CREATE INDEX IF NOT EXISTS idx_friendships_status ON friendships(status);

-- 2. Real-Time Server Presence
CREATE TABLE IF NOT EXISTS user_presence (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE', -- ONLINE, LISTENING, LISTEN_TOGETHER, AWAY, OFFLINE, INVISIBLE
    privacy_mode VARCHAR(20) NOT NULL DEFAULT 'EVERYONE', -- EVERYONE, FRIENDS_ONLY, NOBODY, INVISIBLE
    current_track_id VARCHAR(255),
    current_track_title TEXT,
    current_artist_name TEXT,
    current_thumbnail_url TEXT,
    last_active_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_presence_status ON user_presence(status);

-- 3. Direct Music Messages
CREATE TABLE IF NOT EXISTS direct_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT', -- TEXT, SONG, ALBUM, PLAYLIST, LYRICS, TIMESTAMP, GIFT
    text_content TEXT,
    song_id VARCHAR(255),
    song_title TEXT,
    artist_name TEXT,
    thumbnail_url TEXT,
    timestamp_moment VARCHAR(50),
    is_gift BOOLEAN DEFAULT FALSE,
    is_gift_opened BOOLEAN DEFAULT FALSE,
    delivery_status VARCHAR(20) NOT NULL DEFAULT 'SENT', -- SENDING, SENT, DELIVERED, READ, FAILED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_messages_sender ON direct_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_receiver ON direct_messages(receiver_id);
CREATE INDEX IF NOT EXISTS idx_messages_created ON direct_messages(created_at DESC);

-- 4. Social Music Feed Posts
CREATE TABLE IF NOT EXISTS social_posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_type VARCHAR(20) NOT NULL DEFAULT 'SONG', -- SONG, ALBUM, PLAYLIST, LYRICS, MOMENT, MOOD, REPOST
    song_id VARCHAR(255),
    song_title TEXT,
    artist_name TEXT,
    album_name TEXT,
    thumbnail_url TEXT,
    caption TEXT,
    lyrics_snippet TEXT,
    moment_timestamp VARCHAR(50),
    mood_emoji VARCHAR(10),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC', -- PUBLIC, FRIENDS, PRIVATE
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    reposts_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_posts_author ON social_posts(author_id);
CREATE INDEX IF NOT EXISTS idx_posts_created ON social_posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_posts_visibility ON social_posts(visibility);

-- 5. Post Reactions (Deterministic per-user reaction)
CREATE TABLE IF NOT EXISTS post_reactions (
    post_id UUID NOT NULL REFERENCES social_posts(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reaction_type VARCHAR(20) NOT NULL, -- LOVE, FIRE, FEELS, MIND_BLOWN, HURT, VIBE
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_reactions_post ON post_reactions(post_id);

-- 6. Threaded Post Comments
CREATE TABLE IF NOT EXISTS post_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id UUID NOT NULL REFERENCES social_posts(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    comment_text TEXT NOT NULL,
    likes_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_comments_post ON post_comments(post_id);

-- 7. Collaborative Playlist Members & Roles
CREATE TABLE IF NOT EXISTS playlist_collaborators (
    playlist_id VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'EDITOR', -- OWNER, EDITOR, VIEWER
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (playlist_id, user_id)
);

-- 8. Friend Circles (Squads)
CREATE TABLE IF NOT EXISTS friend_circles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    emoji VARCHAR(10) DEFAULT '🎵',
    shared_playlist_name TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS circle_members (
    circle_id UUID NOT NULL REFERENCES friend_circles(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) DEFAULT 'MEMBER', -- OWNER, ADMIN, MEMBER
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (circle_id, user_id)
);

-- 9. 24-Hour Music Stories
CREATE TABLE IF NOT EXISTS music_stories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    song_title TEXT NOT NULL,
    artist_name TEXT NOT NULL,
    thumbnail_url TEXT,
    caption TEXT,
    lyrics_snippet TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP + INTERVAL '24 hours')
);

CREATE INDEX IF NOT EXISTS idx_stories_expires ON music_stories(expires_at DESC);

-- 10. Interactive Music Polls
CREATE TABLE IF NOT EXISTS music_polls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    creator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    is_closed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS poll_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poll_id UUID NOT NULL REFERENCES music_polls(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    votes_count INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS poll_votes (
    poll_id UUID NOT NULL REFERENCES music_polls(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    option_id UUID NOT NULL REFERENCES poll_options(id) ON DELETE CASCADE,
    voted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (poll_id, user_id)
);

-- 11. User Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sender_id UUID REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(50) NOT NULL, -- FRIEND_REQUEST, FRIEND_ACCEPTED, DIRECT_MESSAGE, SONG_SUGGESTION, ROOM_INVITE, POST_LIKE, COMMENT
    payload_json JSONB DEFAULT '{}'::jsonb,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id, is_read);
