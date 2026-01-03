package com.example.droidspecs.Utils

/**
 * A comprehensive utility to map Android internal Board/Hardware codenames
 * to their actual Marketing Names (e.g., "pineapple" -> "Snapdragon 8 Gen 3").
 *
 * Covers 150+ modern and legacy SoCs.
 */
object SoCUtils {

    fun getMarketingName(board: String, hardware: String): String {
        val b = board.lowercase().trim()
        val h = hardware.lowercase().trim()


        getTensorName(b)?.let { return it }


        getSnapdragonName(b, h)?.let { return it }


        getExynosName(b, h)?.let { return it }


        getMediaTekName(b, h)?.let { return it }


        getKirinName(h)?.let { return it }


        getUnisocName(b, h)?.let { return it }

        return ""
    }

    private fun getTensorName(board: String): String? {
        return when {
            board.contains("zuma") -> "Google Tensor G3"
            board.contains("cloudripper") || board.contains("gs201") || board.contains("cheetah") || board.contains("panther") -> "Google Tensor G2"
            board.contains("gs101") || board.contains("whitefin") || board.contains("oriole") || board.contains("raven") || board.contains("bluejay") -> "Google Tensor"
            else -> null
        }
    }

    private fun getSnapdragonName(board: String, hardware: String): String? {

        val key = if (board.isNotEmpty() && board != "unknown") board else hardware

        return when {

            key.contains("pineapple") || key.contains("lanai") || key.contains("sm8650") -> "Snapdragon 8 Gen 3"
            key.contains("kalama") || key.contains("sm8550") -> "Snapdragon 8 Gen 2"
            key.contains("cape") || key.contains("sm8475") -> "Snapdragon 8+ Gen 1"
            key.contains("taro") || key.contains("sm8450") -> "Snapdragon 8 Gen 1"
            key.contains("lahaina") || key.contains("sm8350") -> "Snapdragon 888 / 888+"
            key.contains("kona") || key.contains("sm8250") -> "Snapdragon 865 / 870"
            key.contains("msmnile") || key.contains("sm8150") -> "Snapdragon 855 / 855+"
            key.contains("napali") || key.contains("sdm845") -> "Snapdragon 845"
            key.contains("msm8998") -> "Snapdragon 835"
            key.contains("msm8996") -> "Snapdragon 820 / 821"


            key.contains("cliffs") || key.contains("sm7550") -> "Snapdragon 7 Gen 3"
            key.contains("crow") || key.contains("sm7475") -> "Snapdragon 7+ Gen 2"
            key.contains("sm7450") -> "Snapdragon 7 Gen 1"
            key.contains("yupik") || key.contains("sm7325") -> "Snapdragon 778G / 778G+ / 782G"
            key.contains("cedros") || key.contains("sm7225") -> "Snapdragon 750G"
            key.contains("lito") || key.contains("sm7250") -> "Snapdragon 765G / 768G"
            key.contains("atoll") || key.contains("sm7125") -> "Snapdragon 720G"
            key.contains("sm7150") -> "Snapdragon 730 / 732G"
            key.contains("sdm710") || key.contains("sdm712") -> "Snapdragon 710 / 712"


            key.contains("parrot") || key.contains("sm6450") -> "Snapdragon 6 Gen 1"
            key.contains("sm6375") -> "Snapdragon 695 5G"
            key.contains("khaje") || key.contains("sm6225") -> "Snapdragon 680"
            key.contains("bengal") || key.contains("sm6115") -> "Snapdragon 662"
            key.contains("trinket") || key.contains("sm6125") -> "Snapdragon 665"
            key.contains("sm6150") -> "Snapdragon 675"
            key.contains("sdm660") -> "Snapdragon 660"
            key.contains("sdm636") -> "Snapdragon 636"
            key.contains("sdm632") -> "Snapdragon 632"
            key.contains("sdm630") -> "Snapdragon 630"
            key.contains("msm8953") -> "Snapdragon 625"


            key.contains("sm4450") -> "Snapdragon 4 Gen 2"
            key.contains("sm4375") -> "Snapdragon 4 Gen 1"
            key.contains("holi") || key.contains("sm4350") -> "Snapdragon 480 / 480+"
            key.contains("sm4250") -> "Snapdragon 460"
            key.contains("sdm450") -> "Snapdragon 450"
            key.contains("sdm439") -> "Snapdragon 439"
            key.contains("sdm429") -> "Snapdragon 429"
            key.contains("msm8940") -> "Snapdragon 435"
            key.contains("msm8937") -> "Snapdragon 430"
            key.contains("msm8917") -> "Snapdragon 425"

            else -> null
        }
    }


    private fun getExynosName(board: String, hardware: String): String? {
        val key = if (hardware.contains("s5e") || hardware.contains("exynos")) hardware else board

        return when {

            key.contains("s5e9945") -> "Exynos 2400"
            key.contains("s5e9925") -> "Exynos 2200"
            key.contains("s5e9840") -> "Exynos 2100"
            key.contains("s5e9830") -> "Exynos 990"
            key.contains("s5e9820") -> "Exynos 9820"
            key.contains("s5e9810") -> "Exynos 9810"
            key.contains("s5e8895") -> "Exynos 8895"


            key.contains("s5e8845") -> "Exynos 1480"
            key.contains("s5e8835") -> "Exynos 1380"
            key.contains("s5e8535") -> "Exynos 1330"
            key.contains("s5e8825") -> "Exynos 1280"
            key.contains("s5e9815") -> "Exynos 1080"
            key.contains("s5e9630") -> "Exynos 980"
            key.contains("s5e8805") -> "Exynos 880"

            key.contains("s5e3830") -> "Exynos 850"
            key.contains("exynos9611") || key.contains("s5e9611") -> "Exynos 9611"
            key.contains("exynos9610") -> "Exynos 9610"
            key.contains("exynos9609") -> "Exynos 9609"
            key.contains("exynos7904") -> "Exynos 7904"
            key.contains("exynos7885") -> "Exynos 7885"
            key.contains("exynos7884") -> "Exynos 7884"
            key.contains("exynos7870") -> "Exynos 7870"
            key.contains("exynos7570") -> "Exynos 7570"

            else -> null
        }
    }


    private fun getMediaTekName(board: String, hardware: String): String? {

        val key = if (hardware.startsWith("mt")) hardware else board

        return when {

            key.contains("mt6989") -> "Dimensity 9300"
            key.contains("mt6985") -> "Dimensity 9200 / 9200+"
            key.contains("mt6983") -> "Dimensity 9000 / 9000+"
            key.contains("mt6895") -> "Dimensity 8100 / 8200"
            key.contains("mt6893") -> "Dimensity 1200 / 1300 / 8020 / 8050"
            key.contains("mt6891") -> "Dimensity 1100"
            key.contains("mt6889") -> "Dimensity 1000+"
            key.contains("mt6885") -> "Dimensity 1000"


            key.contains("mt6886") -> "Dimensity 7200"
            key.contains("mt6877") -> "Dimensity 900 / 920 / 1080 / 7050"
            key.contains("mt6855") -> "Dimensity 930 / 7020"
            key.contains("mt6853") -> "Dimensity 720 / 800U"
            key.contains("mt6833") -> "Dimensity 700 / 6020 / 6080"
            key.contains("mt6873") -> "Dimensity 800"

            key.contains("mt6789") -> "Helio G99 / G100"
            key.contains("mt6785") -> "Helio G90T / G95"
            key.contains("mt6781") -> "Helio G96"
            key.contains("mt6769") -> "Helio G80 / G85"
            key.contains("mt6768") -> "Helio G70 / G88"
            key.contains("mt6765") -> "Helio G35 / G37 / P35"
            key.contains("mt6762") -> "Helio G25 / P22"
            key.contains("mt6761") -> "Helio A22"

            key.contains("mt6779") -> "Helio P90"
            key.contains("mt6771") -> "Helio P60 / P70"
            key.contains("mt6763") -> "Helio P23"
            key.contains("mt6757") -> "Helio P20 / P25"
            key.contains("mt6755") -> "Helio P10"
            key.contains("mt6750") -> "MT6750"
            key.contains("mt6739") -> "MT6739"

            else -> null
        }
    }

    private fun getKirinName(hardware: String): String? {
        val h = hardware.replace(" ", "")
        return when {
            h.contains("kirin9000s") -> "Kirin 9000S (5G)"
            h.contains("kirin9000") -> "Kirin 9000 (5G)"
            h.contains("kirin990") -> "Kirin 990"
            h.contains("kirin985") -> "Kirin 985"
            h.contains("kirin980") -> "Kirin 980"
            h.contains("kirin820") -> "Kirin 820"
            h.contains("kirin810") -> "Kirin 810"

            // Legacy
            h.contains("kirin970") -> "Kirin 970"
            h.contains("kirin960") -> "Kirin 960"
            h.contains("kirin710") -> "Kirin 710 / 710F"
            h.contains("kirin659") -> "Kirin 659"
            h.contains("hi6250") -> "Kirin 650"
            h.contains("hi3660") -> "Kirin 960"
            h.contains("hi3650") -> "Kirin 950"

            else -> null
        }
    }


    private fun getUnisocName(board: String, hardware: String): String? {
        val key = if (board.isNotEmpty()) board else hardware
        return when {
            key.contains("ums9230") || key.contains("t606") -> "Unisoc T606"
            key.contains("ums9230") || key.contains("t616") -> "Unisoc T616"
            key.contains("t612") -> "Unisoc T612"
            key.contains("ums512") || key.contains("t618") -> "Unisoc T618"
            key.contains("ums312") || key.contains("t310") -> "Unisoc T310"
            key.contains("sc9863a") -> "Unisoc SC9863A"
            else -> null
        }
    }
}