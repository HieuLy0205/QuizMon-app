package com.example.quizmon.ui.pet

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.quizmon.data.repository.petReposiroty
import com.example.quizmon.ui.shop.PreferenceManager
import com.example.quizmon.data.model.Pet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizmon.R
import android.widget.ListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import com.example.quizmon.utils.TaskHeadManager


class TupetActivity: AppCompatActivity(){
    //không để tâm
    private lateinit var lv_vat_in_tu: ListView
    private lateinit var img_pet_preview: ImageView
    private lateinit var lv_kho_dung: ListView
    private lateinit var pref: PreferenceManager
    private lateinit var btnBack: ImageButton
    private val reposiroty = petReposiroty()
    private var currentList = listOf<Pet>()
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
        lv_vat_in_tu.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, danh_sach_vat)

        lv_vat_in_tu.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                showListPet()
            } else {
                showListTrung()
            }
        }
        //setup listview bên phải ( phân loại đối tượng )
        // sự kiện chọn pet
        lv_kho_dung.setOnItemClickListener { _, _, position, _ ->
            val selectedPet = currentList[position]
            // Cập nhật khung Preview (Lấy ảnh đầu tiên của level 1 để xem trước)
            val previewImg = selectedPet.animetor[1]?.get(0) ?: 0
            img_pet_preview.setImageResource(previewImg)
            tv_danhsach.text = selectedPet.name
            // Lưu ID để mang về ActivityPet
            pref.savePetid(selectedPet.id.toInt())
            Toast.makeText(this, "Đã chọn: ${selectedPet.name}", Toast.LENGTH_SHORT).show()
        }
        btnBack.setOnClickListener { finish() }
        showListPet()
    }
    private fun showListPet() {
        // 1. Lấy danh sách ID đã sở hữu
        val ownedIds = pref.get_sh_PetIds()

        // 2. Lọc danh sách từ Repository: Chỉ lấy những con có ID nằm trong sh_Ids
        currentList = reposiroty.getAllPets().filter { pet ->
            ownedIds.contains(pet.id)
        }
        // 3. Hiển thị lên ListView
        if (currentList.isEmpty()) {
            lv_kho_dung.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("Bạn chưa có thú cưng nào"))
        } else {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currentList.map { it.name })
            lv_kho_dung.adapter = adapter
        }
    }

    private fun showListTrung() {
        // Giá trị trả về khi hàm get_Sh_EggIds() lục lọi trong bộ nhớ, nó trả veef danh sách
        val ownedEggIds = pref.get_sh_EggIds()
        // biến ánh xạ.map từ id sang tên
        val eggNames = ownedEggIds.map { "Trứng $it" }
        if (eggNames.isEmpty()) {
            lv_kho_dung.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("Bạn chưa có trứng nào"))
        } else {
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