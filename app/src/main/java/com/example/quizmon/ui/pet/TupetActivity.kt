package com.example.quizmon.ui.pet

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.data.repository.petReposiroty
import com.example.quizmon.data.model.Pet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R
import android.widget.ListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
//import androidx.paging.map
import com.example.quizmon.data.model.Trung
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.utils.TaskHeadManager


class TupetActivity : AppCompatActivity() {
    //không để tâm
    private lateinit var lv_vat_in_tu: ListView
    private lateinit var img_pet_preview: ImageView
    private lateinit var lv_kho_dung: ListView
    private lateinit var pref: PreferenceManager
    private lateinit var btnBack: ImageButton
    private val reposiroty = petReposiroty()
    private var currentListpet = listOf<Pet>()
    private var currentListtrung = listOf<Trung>()

    private var vi_tri_pet = true
    private lateinit var tv_danhsach: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tupet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tupet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lv_vat_in_tu = findViewById(R.id.lv_vat_in_tu)
        lv_kho_dung = findViewById(R.id.lv_kho_dung)
        img_pet_preview = findViewById(R.id.img_pet_preview)
        tv_danhsach = findViewById(R.id.tv_selected_pet_name)
        btnBack = findViewById(R.id.btnBack)

        pref = PreferenceManager(this)

        //setup listview bên trái
        val danh_sach_vat = listOf("Thú", "Trứng")
        lv_vat_in_tu.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, danh_sach_vat)

        lv_vat_in_tu.setOnItemClickListener { parent, view, position, id ->
            vi_tri_pet = (position == 0)
            if (vi_tri_pet) {
                showListPet()
            } else {
                showListTrung()
            }
        }
        //setup listview bên phải ( phân loại đối tượng )
        lv_kho_dung.setOnItemClickListener { _, _, position, _ ->
            if (vi_tri_pet) {
                //chế độ pet
                val choice_pet = currentListpet[position]
                // khai báo biến tạo địa chỉ
                val previewImg = currentListpet[position].animetor[1]?.get(0) ?: 0
                //đổi ảnh
                img_pet_preview.setImageResource(previewImg)
                // hiển thị tên pet
                tv_danhsach.text = choice_pet.name
                // lưu id pet
                pref.savePetid(choice_pet.id.toInt())
                //sự kiện nay có ba thành phân
                Toast.makeText(this, "Đã chọn: ${choice_pet.name}", Toast.LENGTH_SHORT).show()
            } else {
                //chế độ trung
                if (currentListtrung.isNotEmpty()) {
                    val choice_trung = currentListtrung[position]
                    val id_trung = choice_trung.id
                    //đổi ảnh
                    img_pet_preview.setImageResource(choice_trung.poto_trung)
                    tv_danhsach.text = choice_trung.name
                    img_pet_preview.postDelayed({
                        // XÓA TRỨNG - THÊM PET (Dùng chung ID vì repo bạn đặt giống nhau)
                        pref.delete_trung(id_trung)
                        pref.add_sh_Pet(id_trung)
                        pref.savePetid(id_trung.toInt())

                        // Tìm con pet vừa nở để hiện ảnh cấp 1 của nó
                        val hatchedPet = reposiroty.getPetById(id_trung)
                        val petImg = hatchedPet?.animetor?.get(1)?.get(0) ?: 0

                        img_pet_preview.setImageResource(petImg)
                        tv_danhsach.text = "Chúc mừng! Đã nở ra ${hatchedPet?.name}"

                        Toast.makeText(this, "Bạn đã nhận được Pet mới!", Toast.LENGTH_SHORT).show()

                        // 3. Cập nhật lại danh sách trứng ngay lập tức (để nó biến mất khỏi list)
                        showListTrung()

                    }, 1000)
                }
            }
        }
        btnBack.setOnClickListener { finish() }
        showListPet()
    }

    private fun showListPet() {
        // 1. Lấy danh sách ID đã sở hữu
        val ownedIds = pref.get_sh_PetIds()

        // 2. Lọc danh sách từ Repository: Chỉ lấy những con có ID nằm trong sh_Ids
        currentListpet = reposiroty.getAllPets().filter { pet ->
            ownedIds.contains(pet.id)
        }
        // 3. Hiển thị lên ListView
        if (currentListpet.isEmpty()) {
            lv_kho_dung.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                listOf("Bạn chưa có thú cưng nào")
            )
        } else {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                currentListpet.map { it.name })
            lv_kho_dung.adapter = adapter
        }
    }

    private fun showListTrung() {
        // Giá trị trả về khi hàm get_Sh_EggIds() lục lọi trong bộ nhớ, nó trả veef danh sách
        val ownedEggIds = pref.get_sh_EggIds()
        // biến ánh xạ.map từ id sang tên
        currentListtrung = reposiroty.getAllTrung().filter { ownedEggIds.contains(it.id) }

        if (currentListtrung.isEmpty()) {
            lv_kho_dung.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                listOf("Bạn chưa có trứng nào")
            )
        } else {
            val eggNames = currentListtrung.map { it.name }
            lv_kho_dung.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, eggNames)
        }
    }

    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.layout_taskhead), pref)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }


}