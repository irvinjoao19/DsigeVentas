package com.dsige.dsigeventas.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import java.io.*
import java.nio.channels.FileChannel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

object Util {

    val FolderImg = "Dsige/Lds"
    val UrlFoto = "http://www.dsige.com/webApiDsigeVehiculo/fotos/"

    private var FechaActual: String? = ""
    private var date: Date? = null

    private const val img_height_default = 800
    private const val img_width_default = 600

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun formatToYesterdayOrToday(date: String): String {
        var day = "Ult. Llamada"
        if (date.isNotEmpty()) {

//            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss a")
//            sdf.timeZone = TimeZone.getTimeZone("GMT")
//            val dateTime = sdf.parse(date)
            val dateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").parse(date)

            val calendar = Calendar.getInstance()
            calendar.time = dateTime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("HH:mm:ss aaa")

            day =
                if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    "HOY " + timeFormatter.format(dateTime)
                } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(
                        Calendar.DAY_OF_YEAR
                    ) == yesterday.get(
                        Calendar.DAY_OF_YEAR
                    )
                ) {
                    "AYER " + timeFormatter.format(dateTime)
                } else {
                    date
                }
        }

        return day
    }

    fun getFormatDate(date: Date): String {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss a")
        return format.format(date)
    }

    fun getFecha(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
//        return "05/10/2019"
    }

    fun getHora(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm aaa")
        return format.format(date)
    }

    fun getFechaActual(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return format.format(date)
    }

    fun getHoraActual(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm:ss aaa")
        return format.format(date)
    }

    fun getFechaEditar(): String? {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return FechaActual
    }

    fun getFechaActualForPhoto(tipo: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return String.format("%s_%s.jpg", tipo, FechaActual)
    }

    fun getFotoName(id: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return String.format("Foto%s_%s.jpg", id, FechaActual)
    }

    fun toggleTextInputLayoutError(textInputLayout: TextInputLayout, msg: String?) {
        textInputLayout.error = msg
        textInputLayout.isErrorEnabled = msg != null
    }

    // TODO SOBRE ADJUNTAR PHOTO

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        val source: FileChannel? = FileInputStream(sourceFile).channel
        val destination: FileChannel = FileOutputStream(destFile).channel
        if (source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        source?.close()
        destination.close()
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        @SuppressLint("Recycle") val cursor =
            Objects.requireNonNull(context).contentResolver.query(
                contentUri,
                proj,
                null,
                null,
                null
            )
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(columnIndex)
        }
        return result
    }

    fun getFolder(): File {
        val folder = File(Environment.getExternalStorageDirectory(), FolderImg)
        if (!folder.exists()) {
            val success = folder.mkdirs()
            if (!success) {
                folder.mkdir()
            }
        }
        return folder
    }

    fun getFolderPhoto(): File {
        val folder = File(Environment.getExternalStorageDirectory(), FolderImg)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }
    // TODO SOBRE FOTO

    fun generateImageAsync(pathFile: String): Observable<Boolean> {
        return Observable.create { e ->
            e.onNext(comprimirImagen(pathFile))
            e.onComplete()
        }
    }

    private fun comprimirImagen(PathFile: String): Boolean {
        return try {
            val result = getRightAngleImage(PathFile)
            result == PathFile
        } catch (ex: Exception) {
            Log.i("exception", ex.message)
            false
        }
    }

    private fun getDateTimeFormatString(date: Date): String {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    fun getDateTimeFormatString(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    private fun processingBitmapSetDateTime(bm1: Bitmap?, captionString: String?): Bitmap? {
        //Bitmap bm1 = null;
        var newBitmap: Bitmap? = null
        try {

            var config: Bitmap.Config? = bm1!!.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            newBitmap = Bitmap.createBitmap(bm1.width, bm1.height, config)

            val newCanvas = Canvas(newBitmap!!)
            newCanvas.drawBitmap(bm1, 0f, 0f, null)

            if (captionString != null) {

                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.color = Color.RED
                paintText.textSize = 22f
                paintText.style = Paint.Style.FILL
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

                val rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString.length, rectText)
                newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
            }

            //} catch (FileNotFoundException e) {
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return newBitmap
    }


    private fun copyBitmatToFile(filename: String, bitmap: Bitmap): String {
        return try {
            val f = File(filename)

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
            val bitmapdata = bos.toByteArray()

            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            "true"

        } catch (ex: IOException) {
            ex.message.toString()
        }

    }


    private fun shrinkBitmap(file: String, width: Int, height: Int): Bitmap {

        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        options.inJustDecodeBounds = true

        val heightRatio = ceil((options.outHeight / height.toFloat()).toDouble()).toInt()
        val widthRatio = ceil((options.outWidth / width.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio
            } else {
                options.inSampleSize = widthRatio
            }
        }

        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(file, options)

    }

    private fun shrinkBitmapOnlyReduce(
        file: String,
        width: Int,
        height: Int,
        captionString: String?
    ) {

        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        options.inJustDecodeBounds = true

        val heightRatio = ceil((options.outHeight / height.toFloat()).toDouble()).toInt()
        val widthRatio = ceil((options.outWidth / width.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio
            } else {
                options.inSampleSize = widthRatio
            }
        }

        options.inJustDecodeBounds = false

        try {


            val b = BitmapFactory.decodeFile(file, options)

            var config: Bitmap.Config? = b.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            val newBitmap = Bitmap.createBitmap(b.width, b.height, config)

            val newCanvas = Canvas(newBitmap)
            newCanvas.drawBitmap(b, 0f, 0f, null)

            if (captionString != null) {

                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.color = Color.RED
                paintText.textSize = 22f
                paintText.style = Paint.Style.FILL
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

                val rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString.length, rectText)
                newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
            }

            val fOut = FileOutputStream(file)
            val imageName = file.substring(file.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(file)
            if (imageType.equals("png", ignoreCase = true)) {
                newBitmap.compress(Bitmap.CompressFormat.PNG, 70, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                    "jpg",
                    ignoreCase = true
                )
            ) {
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }
            fOut.flush()
            fOut.close()
            newBitmap.recycle()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shrinkBitmapOnlyReduceCamera2(
        file: String,
        width: Int,
        height: Int,
        captionString: String?
    ) {

        val targetW: Int = width
        val targetH: Int = height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = (photoW / targetW).coerceAtMost(photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor

        }
//        BitmapFactory.decodeFile(file, bmOptions)?.also { bitmap ->
//            try {
//                val newCanvas = Canvas(bitmap)
//                newCanvas.drawBitmap(bitmap, 0f, 0f, null)
//
//                if (captionString != null) {
//                    val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
//                    paintText.color = Color.RED
//                    paintText.textSize = 22f
//                    paintText.style = Paint.Style.FILL
//                    paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)
//
//                    val rectText = Rect()
//                    paintText.getTextBounds(captionString, 0, captionString.length, rectText)
//                    newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }


        try {


            val b = BitmapFactory.decodeFile(file, bmOptions)

            var config: Bitmap.Config? = b.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            val newBitmap = Bitmap.createBitmap(b.width, b.height, config)

            val newCanvas = Canvas(newBitmap)
            newCanvas.drawBitmap(b, 0f, 0f, null)

            if (captionString != null) {

                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.color = Color.RED
                paintText.textSize = 22f
                paintText.style = Paint.Style.FILL
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

                val rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString.length, rectText)
                newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
            }

            val fOut = FileOutputStream(file)
            val imageName = file.substring(file.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(file)
            if (imageType.equals("png", ignoreCase = true)) {
                newBitmap.compress(Bitmap.CompressFormat.PNG, 70, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                    "jpg",
                    ignoreCase = true
                )
            ) {
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }
            fOut.flush()
            fOut.close()
            newBitmap.recycle()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // TODO SOBRE ROTAR LA PHOTO

    fun getRightAngleImage(photoPath: String): String {

        try {
            val ei = ExifInterface(photoPath)
            val orientation =
                ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val degree: Int

            degree = when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }

            return rotateImage(degree, photoPath)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return photoPath
    }

    private fun rotateImage(degree: Int, imagePath: String): String {
        if (degree <= 0) {
            shrinkBitmapOnlyReduce(
                imagePath,
                img_width_default,
                img_height_default,
                getDateTimeFormatString(Date(File(imagePath).lastModified()))
            )
            return imagePath
        }
        try {

            var b: Bitmap? = shrinkBitmap(imagePath, img_width_default, img_height_default)
            val matrix = Matrix()
            if (b!!.width > b.height) {
                matrix.setRotate(degree.toFloat())
                b = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)
                b = processingBitmapSetDateTime(
                    b,
                    getDateTimeFormatString(Date(File(imagePath).lastModified()))
                )
            }

            val fOut = FileOutputStream(imagePath)
            val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(imagePath)
            if (imageType.equals("png", ignoreCase = true)) {
                b!!.compress(Bitmap.CompressFormat.PNG, 70, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                    "jpg",
                    ignoreCase = true
                )
            ) {
                b!!.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }
            fOut.flush()
            fOut.close()
            b!!.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
    }

    fun getVersion(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return pInfo.versionName
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getImei(context: Context): String {
        val deviceUniqueIdentifier: String
        val telephonyManager: TelephonyManager? =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        deviceUniqueIdentifier = if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else {
                @Suppress("DEPRECATION")
                telephonyManager.deviceId
            }
        } else {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
        return deviceUniqueIdentifier
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences("TOKEN", MODE_PRIVATE).getString("token", "empty")
    }

    fun getNotificacionValid(context: Context): String? {
        return context.getSharedPreferences("TOKEN", MODE_PRIVATE).getString("update", "")
    }

    fun updateNotificacionValid(context: Context) {
        context.getSharedPreferences("TOKEN", MODE_PRIVATE).edit().putString("update", "").apply()
    }

    fun snackBarMensaje(view: View, mensaje: String) {
        val mSnackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT)
        mSnackbar.setAction("Ok") { mSnackbar.dismiss() }
        mSnackbar.show()
    }

    fun toastMensaje(context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
    }

    fun dialogMensaje(context: Context, title: String, mensaje: String) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

    // TODO CLOSE TECLADO

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(edit: EditText, context: Context) {
        edit.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        // TODO FOR FRAGMENTS
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getDateDialogText(context: Context, text: TextView) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val month =
                if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"
            text.text = fecha
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun getDateDialog(context: Context, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val month =
                if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"
            input.setText(fecha)
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun getHourDialog(context: Context, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val timePickerDialog =
            TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                val minutes = if (minute < 10) "0$minute" else minute.toString()
                val day = if (hourOfDay < 12) "a.m." else "p.m."
                input.setText(String.format("%s:%s %s", hour, minutes, day))
            }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    private fun getCompareFecha(fechaInicial: String, fechaFinal: String): Boolean {
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        var date1 = Date()
        try {
            date1 = format.parse(fechaFinal)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var date2 = Date()
        try {
            date2 = format.parse(fechaInicial)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date1.before(date2)
    }

    fun getDateDialog(context: Context, view: View, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year
                                                           , monthOfYear, dayOfMonth ->
            val month =
                if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"

            if (!getCompareFecha(getFecha(), fecha)) {
                input.setText(fecha)
            } else {
                snackBarMensaje(view, "Fecha Propuesta no debe ser menor a la fecha actual")
            }

        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun getTextHTML(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }

        //    Util.getTextHTML("<font color='red'>Cant. Galones</font> : " + h.cantidad),
        //                BufferType.SPANNABLE
    }

    fun isNumeric(strNum: String): Boolean {
        try {
            val d = Integer.parseInt(strNum)
            Log.i("TAG", d.toString())
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }

    fun isDecimal(s: String): Boolean {
        try {
            val d = s.toDouble()
            Log.i("TAG", d.toString())
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }

    @Throws(IOException::class)
    fun deleteDirectory(file: File) {
        if (file.isDirectory) {
            for (ct: File in file.listFiles()) {
                ct.delete()
            }
        }
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f) shl shift
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }
}