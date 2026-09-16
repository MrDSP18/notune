package com.music.echo.notune.personalization

import com.music.echo.notune.personalization.model.SelectedArtist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasteSeedingEngine @Inject constructor() {
    
    fun getArtistsForLanguages(languages: Set<String>): List<SelectedArtist> {
        val result = mutableListOf<SelectedArtist>()
        languages.forEach { lang ->
            val artists = languageToArtists[lang] ?: emptyList()
            result.addAll(artists)
        }
        return result.distinctBy { it.name }.shuffled().take(30)
    }

    fun getGenresForLanguages(languages: Set<String>): List<String> {
        val result = mutableListOf<String>()
        languages.forEach { lang ->
            val genres = languageToGenres[lang] ?: emptyList()
            result.addAll(genres)
        }
        // Add global genres
        result.addAll(listOf("Pop", "Rock", "Hip-Hop", "Electronic", "R&B", "Indie", "Classical", "Jazz", "Metal", "Ambient"))
        return result.distinct().sorted()
    }

    private val languageToArtists = mapOf(
        "Tamil" to listOf(
            SelectedArtist("1", "A.R. Rahman"), SelectedArtist("2", "Anirudh Ravichander"),
            SelectedArtist("3", "Yuvan Shankar Raja"), SelectedArtist("4", "Santhosh Narayanan"),
            SelectedArtist("5", "Harris Jayaraj"), SelectedArtist("6", "Sid Sriram"),
            SelectedArtist("7", "Dhibu Ninan Thomas"), SelectedArtist("8", "Ilaiyaraaja"),
            SelectedArtist("9", "Pradeep Kumar"), SelectedArtist("10", "Sean Roldan"),
            SelectedArtist("11", "Hiphop Tamizha"), SelectedArtist("12", "Vijay Antony"),
            SelectedArtist("13", "S.P. Balasubrahmanyam"), SelectedArtist("14", "K.S. Chithra"),
            SelectedArtist("15", "Shweta Mohan"), SelectedArtist("16", "GV Prakash Kumar"),
            SelectedArtist("17", "Leon James"), SelectedArtist("18", "Sam C.S.")
        ),
        "Hindi" to listOf(
            SelectedArtist("101", "Arijit Singh"), SelectedArtist("102", "Pritam"),
            SelectedArtist("103", "A.R. Rahman"), SelectedArtist("104", "Amit Trivedi"),
            SelectedArtist("105", "Vishal-Shekhar"), SelectedArtist("106", "Shreya Ghoshal"),
            SelectedArtist("107", "Badshah"), SelectedArtist("108", "Nucleya"),
            SelectedArtist("109", "Anuv Jain"), SelectedArtist("110", "Ritviz"),
            SelectedArtist("111", "Tanishk Bagchi"), SelectedArtist("112", "Mohit Chauhan"),
            SelectedArtist("113", "Sonu Nigam"), SelectedArtist("114", "Atif Aslam"),
            SelectedArtist("115", "Jubin Nautiyal"), SelectedArtist("116", "Neha Kakkar")
        ),
        "Telugu" to listOf(
            SelectedArtist("201", "Devi Sri Prasad"), SelectedArtist("202", "S. Thaman"),
            SelectedArtist("203", "M.M. Keeravani"), SelectedArtist("204", "Anurag Kulkarni"),
            SelectedArtist("205", "Sid Sriram"), SelectedArtist("206", "Vivek Sagar"),
            SelectedArtist("207", "Mickey J. Meyer"), SelectedArtist("208", "Ram Miriyala"),
            SelectedArtist("209", "Mangli"), SelectedArtist("210", "Armaan Malik")
        ),
        "Malayalam" to listOf(
            SelectedArtist("301", "Sushin Shyam"), SelectedArtist("302", "Vishnu Vijay"),
            SelectedArtist("303", "Hesham Abdul Wahab"), SelectedArtist("304", "K.S. Harisankar"),
            SelectedArtist("305", "Jakes Bejoy"), SelectedArtist("306", "Gopi Sundar"),
            SelectedArtist("307", "Shaan Rahman"), SelectedArtist("308", "K.J. Yesudas")
        ),
        "Kannada" to listOf(
            SelectedArtist("701", "Arjun Janya"), SelectedArtist("702", "B. Ajaneesh Loknath"),
            SelectedArtist("703", "Charan Raj"), SelectedArtist("704", "Vijay Prakash"),
            SelectedArtist("705", "Sanjith Hegde"), SelectedArtist("706", "Ravi Basrur")
        ),
        "English" to listOf(
            SelectedArtist("401", "The Weeknd"), SelectedArtist("402", "Taylor Swift"),
            SelectedArtist("403", "Drake"), SelectedArtist("404", "Kendrick Lamar"),
            SelectedArtist("405", "Billie Eilish"), SelectedArtist("406", "Ed Sheeran"),
            SelectedArtist("407", "Dua Lipa"), SelectedArtist("408", "Post Malone"),
            SelectedArtist("409", "Justin Bieber"), SelectedArtist("410", "Ariana Grande"),
            SelectedArtist("411", "Bruno Mars"), SelectedArtist("412", "Coldplay"),
            SelectedArtist("413", "Eminem"), SelectedArtist("414", "Travis Scott"),
            SelectedArtist("415", "Tame Impala"), SelectedArtist("416", "M83")
        ),
        "Korean" to listOf(
            SelectedArtist("501", "BTS"), SelectedArtist("502", "BLACKPINK"),
            SelectedArtist("503", "NewJeans"), SelectedArtist("504", "IU"),
            SelectedArtist("505", "Stray Kids"), SelectedArtist("506", "TWICE")
        ),
        "Punjabi" to listOf(
            SelectedArtist("601", "Sidhu Moose Wala"), SelectedArtist("602", "Diljit Dosanjh"),
            SelectedArtist("603", "AP Dhillon"), SelectedArtist("604", "Karan Aujla"),
            SelectedArtist("605", "Shubh"), SelectedArtist("606", "Gurdas Maan")
        )
    )

    private val languageToGenres = mapOf(
        "Tamil" to listOf("Kollywood", "Tamil Pop", "Tamil Folk", "Tamil Rock", "Carnatic"),
        "Hindi" to listOf("Bollywood", "Indipop", "Desi Hip-Hop", "Ghazal", "Sufi"),
        "Telugu" to listOf("Tollywood", "Telugu Pop", "Telugu Folk"),
        "Malayalam" to listOf("Mollywood", "Malayalam Pop"),
        "Kannada" to listOf("Sandalwood", "Kannada Pop"),
        "Korean" to listOf("K-Pop", "K-Drama OST", "K-HipHop"),
        "Japanese" to listOf("J-Pop", "Anime OST", "J-Rock"),
        "Punjabi" to listOf("Punjabi Pop", "Bhangra", "Punjabi Hip-Hop")
    )
}
