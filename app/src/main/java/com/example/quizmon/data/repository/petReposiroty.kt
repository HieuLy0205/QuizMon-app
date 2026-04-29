package com.example.quizmon.data.repository
import com.example.quizmon.R
import com.example.quizmon.data.model.Pet


class petReposiroty {
    //chứa danh sách pet
    private val allpets = listOf(
        Pet(
            "1",
            "Hỏa long",
            1,
            3,
            mapOf(
                1 to intArrayOf(R.drawable.dragon_c1_f1,
                    R.drawable.dragon_c1_f2),
                2 to intArrayOf(R.drawable.dragon_c2_f1,
                    R.drawable.dragon_c2_f2),
                3 to intArrayOf(R.drawable.dragon_pet_2,
                    R.drawable.dragon_pet_1)
            )
        ),
        Pet(
            "2",
            "Sầu riêng",
            1,
            3,
            mapOf(
                1 to intArrayOf(R.drawable.pet003_xaubong_c1),
                2 to intArrayOf(R.drawable.pet003_xaubong_c2),
                3 to intArrayOf(R.drawable.pet003_xaubong_c3)
            )
        ),
        Pet(
            "3",
            "Mựt Nily ",
            1,
            3,
            mapOf(
                1 to intArrayOf(R.drawable.pet002_tuot_c1),
                2 to intArrayOf(R.drawable.pet002_tuot_c2),
                3 to intArrayOf(R.drawable.pet002_tuot_c3)
            )
        ),
        Pet(
            "4",
            "Sao Nhị (Kỳ cáo băn phong)",
            1,
            3,
            mapOf(
                1 to intArrayOf(R.drawable.pet004_saonhi_c1),
                2 to intArrayOf(R.drawable.pet004_saonhi_c2),
                3 to intArrayOf(R.drawable.pet004_saonhi_c3)
            )
        )
    )
    fun getPetById(id: String): Pet? = allpets.find { it.id == id }

    fun getAllPets(): List<Pet> = allpets

}