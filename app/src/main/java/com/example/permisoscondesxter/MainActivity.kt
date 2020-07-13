package com.example.permisoscondesxter
import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.permisoscondesxter.ENUN.PermissionStatus
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity()  {
     val  context =this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listainersKeys()


    }

    private fun listainersKeys() {


        buttonAll.setOnClickListener {

            AllPermisos()

        }


        buttonAudio.setOnClickListener {
            Audiopermission()
        }
        buttonContac.setOnClickListener {
            checkcontacpermission()
        }

        buttonCamera.setOnClickListener {
            checkpermissions()
        }

    }

    private fun AllPermisos() {

        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.READ_CONTACTS
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    report?.let {
                        for (permissions in report.grantedPermissionResponses){
                            when(permissions.permissionName){
                                android.Manifest.permission.CAMERA->{
                                    SetpermiSionStatus(textViewcamera,PermissionStatus.GAranred)
                                }
                                android.Manifest.permission.READ_CONTACTS->{
                                    SetpermiSionStatus(textViewContac,PermissionStatus.GAranred)
                                }

                                android.Manifest.permission.RECORD_AUDIO->{
                                    SetpermiSionStatus(textViewAudio,PermissionStatus.GAranred)
                                }
                            }
                        }
                        for (permissions in report.deniedPermissionResponses){
                            when(permissions.permissionName){
                                android.Manifest.permission.CAMERA->{
                                    if (permissions.isPermanentlyDenied){
                                        SetpermiSionStatus(textViewcamera,PermissionStatus.NEver)

                                    }else{
                                        SetpermiSionStatus(textViewcamera,PermissionStatus.Denied)
                                    }

                                }

                                android.Manifest.permission.READ_CONTACTS->{
                                    if(permissions.isPermanentlyDenied){
                                        SetpermiSionStatus(textViewContac,PermissionStatus.NEver)
                                    }else{
                                        SetpermiSionStatus(textViewContac,PermissionStatus.Denied)
                                    }
                                }
                                android.Manifest.permission.RECORD_AUDIO->{
                                    if (permissions.isPermanentlyDenied){
                                        SetpermiSionStatus(textViewAudio,PermissionStatus.NEver)

                                    }else{
                                        SetpermiSionStatus(textViewAudio,PermissionStatus.Denied)
                                    }
                                }

                            }
                        }
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()

                }

            }).check()



    }

    private fun Audiopermission() =PermissionHandler(android.Manifest.permission.RECORD_AUDIO,textViewAudio)

    private fun checkcontacpermission() =PermissionHandler(android.Manifest.permission.READ_CONTACTS,textViewContac)

    private fun checkpermissions() =SetcameraPermissionWithDialog()
            //=PermissionHandler(android.Manifest.permission.CAMERA,textViewcamera)

    private fun PermissionHandler(permissions:String,textView: TextView){

        Dexter.withActivity(this)
            .withPermission(permissions)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    SetpermiSionStatus(textView,PermissionStatus.GAranred)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    if (response!!.isPermanentlyDenied!!){
                        SetpermiSionStatus(textView,PermissionStatus.NEver)

                    }else{
                        SetpermiSionStatus(textView,PermissionStatus.Denied)
                    }
                }

            }).check()


    }

    private fun SetpermiSionStatus(textView: TextView,status: PermissionStatus){
        when(status){
            PermissionStatus.Denied->{
                textView.text=getString(R.string.Permiso_negado)
                textView.setTextColor(ContextCompat.getColor( context,  R.color.colornegado))
            }

            PermissionStatus.GAranred->{
                textView.text=getString(R.string.permisoOk)
                textView.setTextColor(ContextCompat.getColor( context,  R.color.colorpermisook))
            }
            PermissionStatus.NEver->{
                textView.text=getString(R.string.Permiso_fueCompletamenteNegado)
                textView.setTextColor(ContextCompat.getColor( context,  R.color.colornegado))
            }

        }
    }


    private fun  SetcameraPermissionWithDialog(){
        val dialogPermissionsListener=DialogOnDeniedPermissionListener.Builder
            .withContext(this)
            .withTitle("richiesta di permessi")
            .withMessage("il permesso è richiesto per effettuare la foto")
            .withButtonText(android.R.string.ok)
            .withIcon(R.mipmap.ic_launcher)
            
            .build()
        val permissionss  = object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                SetpermiSionStatus(textViewcamera,PermissionStatus.GAranred)

            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                if (response?.isPermanentlyDenied!!){
                    SetpermiSionStatus(textViewcamera,PermissionStatus.NEver)
                }

            }

        }

            val compositePermissionListener=CompositePermissionListener(permissionss,dialogPermissionsListener)
        val withListener = Dexter.withActivity(this)
            .withPermission(android.Manifest.permission.CAMERA)
            .withListener( compositePermissionListener)
            .check()


    }

    }



