package uqbar.org.basicrestapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Clase POJO o Bean donde se inyecta el JSON en un objeto de la SDK de Android
 */
class Greeting {

    var id: String? = null
    var content: String? = null
}

/**
 * Interface que contiene el cascarón que retrofit arme la implementación
 */
interface GreetingService {

    @GET("/greeting")
    fun getGreeting(): Observable<Greeting>

    companion object {
        fun create(): GreetingService {
            val BASE_URL = "http://rest-service.guides.spring.io"

            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(GreetingService::class.java)

        }
    }

}

class MainActivity : AppCompatActivity() {

    val greetingService by lazy {
        GreetingService.create()
    }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        armarSaludo()
    }

    private fun armarSaludo() {
        disposable =
                greetingService.getGreeting()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { saludo ->
                                    lblId.text = saludo.id
                                    lblContent.text = saludo.content
                                },
                                { error ->
                                    Toast.makeText(this@MainActivity.applicationContext, "Ocurrió un error al buscar el saludo. ", Toast.LENGTH_LONG).show()
                                    Log.e("BasicRestApp", error.message)
                                }
                        )

    }
}
