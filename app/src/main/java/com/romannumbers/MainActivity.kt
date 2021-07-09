package com.romannumbers

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.romannumbers.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var animation : Animation
    private var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListeners()
    }
    private fun init() {
        animation = AnimationUtils.loadAnimation(context, R.anim.circle_explosion_anim).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            binding.apply {
                fab.isVisible = false
                circle.isVisible = true
                circle.startAnimation(animation){
                    root.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_700))
                    circle.isVisible = false
                    setViews()
                }
            }
        }
        binding.btnConvert.setOnClickListener {
            val text = binding.edtConverter.text.toString()
            if(text.isNotBlank()){
                val romanNumber = romanToInt(text.toUpperCase(Locale.ROOT))
                binding.convertedText.text = "$romanNumber"
            }
        }

        binding.btnConvertToRoman.setOnClickListener {
            val number = binding.edtConverterRoman.text.toString()
            if(number.isNotBlank()){
                val intNumber = intToRoman(number.toInt())
                binding.convertedText2.text = intNumber
            }
        }

        binding.edtConverter.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(checkRoman()){
                    binding.btnConvert.callOnClick()
                }
                else {
                    hideKeyboard()
                    showSnackbar("El numero romano ingresado no es valido")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.edtConverterRoman.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(checkValue()){
                    binding.btnConvertToRoman.callOnClick()
                }
                else{
                    binding.edtConverterRoman.setText("")
                    showSnackbar("El numero a convertir no puede ser mayor a 4000")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun showSnackbar(error : String){
        val snackbar = Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun romanToInt(s: String): Int {
        var sum = 0
        s.forEachIndexed { index, _ ->
            when(s[index]){
                'I' -> {
                    if (index + 1 != s.length && isSubsNumber(s.substring(index, index + 2))) {
                        sum -= 1
                    } else {
                        sum += 1
                    }
                }
                'V' -> sum += 5
                'X' -> {
                    if (index + 1 != s.length && isSubsNumber(s.substring(index, index + 2))) {
                        sum -= 10
                    } else {
                        sum += 10
                    }
                }
                'L' -> sum += 50
                'C' -> {
                    if (index + 1 != s.length && isSubsNumber(s.substring(index, index + 2))) {
                        sum -= 100
                    } else {
                        sum += 100
                    }
                }
                'D' -> sum += 500
                'M' -> sum += 1000
                else-> sum += 0
            }
        }
        return sum
    }

    private fun isSubsNumber(substring: String): Boolean {
        if(substring[0] == 'I' && substring[1] == 'V' || substring[0] == 'I' && substring[1] == 'X'){
            return true
        }
        if(substring[0] == 'X' && substring[1] == 'L' || substring[0] == 'X' && substring[1] == 'C') {
            return true
        }
        if(substring[0] == 'C' && substring[1] == 'D' || substring[0] == 'C' && substring[1] == 'M'){
            return true
        }
        return false
    }

    private fun checkValue(): Boolean {
        try{
            val number = binding.edtConverterRoman.text.toString().toInt()
            if(number > 4000){
                hideKeyboard()
                return false
            }
            return true
        }
        catch (e: NumberFormatException){
            return true
        }
    }

    private fun checkRoman() : Boolean {
        val regex = Regex(pattern = "^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$", options = setOf(RegexOption.IGNORE_CASE))
        return binding.edtConverter.text.toString().isBlank() || regex.matches(binding.edtConverter.text.toString())
    }

    private fun intToRoman(num: Int): String {
        // storing roman values of digits from 0-9
        // when placed at different places
        val m = arrayOf("", "M", "MM", "MMM")
        val c = arrayOf("", "C", "CC", "CCC", "CD", "D",
                "DC", "DCC", "DCCC", "CM")
        val x = arrayOf("", "X", "XX", "XXX", "XL", "L",
                "LX", "LXX", "LXXX", "XC")
        val i = arrayOf("", "I", "II", "III", "IV", "V",
                "VI", "VII", "VIII", "IX")

        // Converting to roman
        val thousands = m[num / 1000]
        val hundreds = c[num % 1000 / 100]
        val tens = x[num % 100 / 10]
        val ones = i[num % 10]
        return thousands + hundreds + tens + ones
    }

    private fun setViews(){
        binding.apply {
            imgRoman.isVisible = true
            converter.isVisible = true
            convertedText.isVisible = true
            converter2.isVisible = true
            convertedText2.isVisible = true
        }
    }

    private fun hideKeyboard(){
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}