package com.example.quizmon.ui.shop
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import com.example.quizmon.R
import com.example.quizmon.utils.PreferenceManager
import com.example.quizmon.ui.pet.AnimetorActivity
import com.example.quizmon.ui.pet.TupetActivity
import com.example.quizmon.utils.TaskHeadManager

class shop_pvp: AppCompatActivity() {
    private lateinit var pref: PreferenceManager
    private lateinit var btnNhanPet1: Button
    private lateinit var btnNhanPet2: Button
    private lateinit var btnNhanPet3: Button
    private lateinit var btnNhanPetFree: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_pvp)
        pref = PreferenceManager(this)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnNhanPetFree = findViewById(R.id.btn_nhanpetFree)
        btnNhanPet1 = findViewById(R.id.btn_nhanpet1)
        btnNhanPet2 = findViewById(R.id.btn_nhanpet2)
        btnNhanPet3 = findViewById(R.id.btn_nhanpet3)
        btnBack.setOnClickListener { finish() }
        setupnhanpet()

    }

    fun setupnhanpet() {
        if (pref.get_sh_EggIds().contains("1")) {
            btnNhanPetFree.isEnabled = false
            btnNhanPetFree.text = "Đã nhận"
        }
        if(pref.get_sh_EggIds().contains("2")){
            btnNhanPet1.isEnabled = false
            btnNhanPet1.text = "Đã nhận"
        }
        if(pref.get_sh_EggIds().contains("3")){
            btnNhanPet2.isEnabled = false
            btnNhanPet2.text = "Đã nhận"
        }
        if(pref.get_sh_EggIds().contains("4")){
            btnNhanPet3.isEnabled = false
            btnNhanPet3.text = "Đã nhận"
        }

        btnNhanPetFree.setOnClickListener {
            val isReady = pref.Dk_xacnhan_cq("nh_trung_1")
            // Kiểm tra thêm lần nữa để chắc chắn chưa nhận
            if (pref.get_sh_EggIds().contains("1")) {
                Toast.makeText(this, "Bạn đã sở hữu trứng này rồi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isReady) {
                pref.add_sh_Egg("1")
                pref.Xn_va_inday("nh_trung_1")
                pref.Dk_batmo_xn("nh_trung_1", false)
                btnNhanPetFree.isEnabled = false
                btnNhanPetFree.text = "Đã nhận"
                Toast.makeText(this, "Nhận trứng thành công!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Chưa xong ải", Toast.LENGTH_SHORT).show()
            }
        }

        btnNhanPet1.setOnClickListener {
            val currentXu = pref.getXu()
            val ds_trung = pref.get_sh_EggIds()
            val gia_xu = 30;
            val id_trung = "2"
            if (!ds_trung.contains(id_trung)) {
                if (currentXu >= gia_xu ) {
                    pref.add_sh_Egg(id_trung)
                    pref.saveXu(currentXu - gia_xu)
                    pref.Dk_batmo_xn("nv4", true)
                    btnNhanPet1.isEnabled = false
                    btnNhanPet1.text = "Đã nhận"
                    Toast.makeText(this, "Nhận trứng thành công!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Bạn không đủ xu!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnNhanPet2.setOnClickListener {
            val currentXu = pref.getXu()
            val ds_trung = pref.get_sh_EggIds()
            val gia_xu = 150
            val id_trung = "3"
            if(!ds_trung.contains(id_trung)){
                if (currentXu >= gia_xu) {
                    pref.add_sh_Egg(id_trung)
                    pref.saveXu(currentXu - gia_xu)
                    pref.Dk_batmo_xn("nv4", true)
                    btnNhanPet2.isEnabled = false
                    btnNhanPet2.text = "Đã nhận"
                    Toast.makeText(this, "Nhận trứng thành công!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Bạn không đủ xu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnNhanPet3.setOnClickListener {
            val currentXu = pref.getXu()
            val ds_trung = pref.get_sh_EggIds()
            val gia_xu = 100
            val id_trung = "4"
            if (!ds_trung.contains(id_trung)){
                if (currentXu >= gia_xu) {
                    pref.saveXu(currentXu - gia_xu)
                    pref.add_sh_Egg(id_trung)
                    pref.Dk_batmo_xn("nv4", true)
                    btnNhanPet3.isEnabled = false
                    btnNhanPet3.text = "Đã nhận"
                    Toast.makeText(this, "Nhận trứng thành công!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Bạn không đủ xu!", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
    override fun onResume() {
        super.onResume()
        TaskHeadManager.startLoop(findViewById(R.id.taskhead), pref)
    }

    override fun onPause() {
        super.onPause()
        TaskHeadManager.stopLoop()
    }
}
