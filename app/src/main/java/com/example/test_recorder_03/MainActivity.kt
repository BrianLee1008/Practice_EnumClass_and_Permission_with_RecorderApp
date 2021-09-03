package com.example.test_recorder_03

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.test_recorder_03.State.*
import com.example.test_recorder_03.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	/*enum class에 두개의 속성이 붇는다.
	* 1. enum class에 따라 달라지는 customView icon / enum class는 인스턴스 생성이 불가하기 때문에 setter를 주어 사용 가능하게 해야한다. 그래도 편의상 enum 인스턴스라고 부르겠다.
	* 2. enum class에 따라 달라지는 기능 메서드 / a메서드가 실행되면 자동적으로 state가 변하면서 자동 다음 메서드 실행 준비*/

	private var state = BEFORE_RECORDING
		set(value) {
			field = value //field는 state 변수라고 보면 된다. 즉 set에 들어오는 value 변수가 고대로 state 변수에 들어감.
			binding.run {
				recordButton.updateIconWithState(value)
				resetButton.isEnabled = value == ON_PLAYING || value == AFTER_RECORDING
			}
		}

	private lateinit var binding: ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		defaultRecordButtonIcon()
		enumInstance()

		setListener()

		requestAudioPermission()


	}

	private fun defaultRecordButtonIcon(){
		binding.recordButton.updateIconWithState(state) // 초기 값인 BEFORE_RECORDING 로 설정
	}

	//설정한 enum인스턴스 메서드
	private fun enumInstance(){
		state = BEFORE_RECORDING
	}

	// 이 아래서부터 각 State에 맞는 기능 메서드 제작 한 다음 각 메서드 호출시 기능 구현하고 마지막으로 state 값 변하는 것까지 해줄 것임


	private fun setListener(){
		binding.run {
			recordButton.setOnClickListener{
				when(state){ // 각각 다음 상태인 메서드로 넘어간다.
					BEFORE_RECORDING -> startRecording()
					ON_RECORDING -> stopRecording()
					AFTER_RECORDING -> startPlaying()
					ON_PLAYING -> stopPlaying()
				}

			}
			resetButton.setOnClickListener {
				state = BEFORE_RECORDING
			}
		}
	}

	private fun startRecording(){
		// TODO MediaRecorder setting 한 다음 file path 만들어 캐시 저장. 그후 prepare 하고 start.

		state = ON_PLAYING
	}

	private fun stopRecording(){
		// TODO MediaRecorder stop 한 다음 release. 그다음 다시 null을 줘 MediaRecorder를 비워준다.

		state = AFTER_RECORDING
	}

	private fun startPlaying(){
		// TODO MediaPlayer setting 할떄 Source로 file path에 저장되어있는 absoluteUri 가져와 start

		state = ON_PLAYING
	}

	private fun stopPlaying(){
		// TODO MediaPlayer release 한 다음 null 주기.

		state = AFTER_RECORDING // 다시 play 할 수 있게...
	}

	private fun requestAudioPermission() {
		val audioPermission = Manifest.permission.RECORD_AUDIO
		val checkSelfPermission = ContextCompat.checkSelfPermission(
			this,
			audioPermission
		) == PackageManager.PERMISSION_GRANTED
		val showEducationUi = shouldShowRequestPermissionRationale(audioPermission)

		when {
			checkSelfPermission -> {
				// 권한 이미 승인 되었으니 유지
			}
			showEducationUi -> {
				// 권한 이미 거부 되었으니 AlertDialog 띄우기
				alertDialog()
			}
			else -> {
				// 위 두개 전부 해당 안되니 requestPermission 팝업 띄우기
				requestPermissions(arrayOf(audioPermission),REQUEST_CODE_RECORD_PERMISSION)
			}

		}

	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (grantResults.isNotEmpty()){
			when(requestCode){
				REQUEST_CODE_RECORD_PERMISSION -> {
					if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED){
						// 예외처리 다 통과하고 requestCode가 승인 되었으면 고대로 유지
					}else{
						// 사용자가 승인 거부한 것이니 AlertDialog 띄우기
						alertDialog()
					}

				}
				else -> {
					// 다른 requestCode 없으니 유지
				}
			}

		} else {
			Toast.makeText(this, "권한승인 팝업 오류입니다.", Toast.LENGTH_SHORT).show()
		}
	}

	private fun alertDialog(){
		AlertDialog.Builder(this)
			.setTitle("권한이 필요합니다.")
			.setMessage("오디오 사용에 대한 권한이 필요합니다. 사용자 설정에서 승인해주세요")
			.setPositiveButton("동의하기") { _, _ ->
				navigationToAppSetting()
			}
			.setNegativeButton("앱 종료하기") { _, _ ->
				finish()
			}
			.create().show()
	}

	private fun navigationToAppSetting(){
		val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS) // 디바이스 세팅의 사용자 설정으로 이동
		val uri : Uri = Uri.fromParts("package",packageName, null)
		intent.data = uri
		startActivity(intent)
	}


	companion object {
		private const val REQUEST_CODE_RECORD_PERMISSION = 201
	}

}