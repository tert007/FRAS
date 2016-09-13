package com.example.alexander.fastreading;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexander.fastreading.reader.ReaderActivity;
import com.example.alexander.fastreading.trainingmenu.TrainingMenuActivity;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    ImageView startTrainingMenuImageView;
    ImageView startReaderImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        startTrainingMenuImageView = (ImageView) findViewById(R.id.main_menu_start_training_menu_text_view);
        startTrainingMenuImageView.setOnClickListener(this);

        startReaderImageView = (ImageView) findViewById(R.id.main_menu_start_reader_text_view);
        startReaderImageView.setOnClickListener(this);

/*
        String text =
                "I" +
                        "\n" +

                        "Был холодный ясный апрельский день, и часы пробили тринадцать. Уткнув подбородок в грудь, чтобы спастись от злого ветра, Уинстон Смит торопливо шмыгнул за стеклянную дверь жилого дома «Победа», но все-таки впустил за собой вихрь зернистой пыли.\n" +
                "\n" +
                "В вестибюле пахло вареной капустой и старыми половиками. Против входа на стене висел цветной плакат, слишком большой для помещения. На плакате было изображено громадное, больше метра в ширину, лицо – лицо человека лет сорока пяти, с густыми черными усами, грубое, но по-мужски привлекательное. Уинстон направился к лестнице. К лифту не стоило и подходить. Он даже в лучшие времена редко работал, а теперь в дневное время электричество вообще отключали. Действовал режим экономии – готовились к Неделе ненависти. Уинстону предстояло одолеть семь маршей; ему шел сороковой год, над щиколоткой у него была варикозная язва; он поднимался медленно и несколько раз останавливался передохнуть. На каждой площадке со стены глядело все то же лицо. Портрет был выполнен так, что, куда бы ты ни стал, глаза тебя не отпускали. СТАРШИЙ БРАТ СМОТРИТ НА ТЕБЯ – гласила подпись.\n" +
                "\n" +
                "В квартире сочный голос что-то говорил о производстве чугуна, зачитывал цифры. Голос шел из заделанной в правую стену продолговатой металлической пластины, похожей на мутное зеркало. Уинстон повернул ручку, голос ослаб, но речь по-прежнему звучала внятно. Аппарат этот (он назывался телекран) притушить было можно, полностью же выключить – нельзя. Уинстон отошел к окну: невысокий тщедушный человек, он казался еще более щуплым в синем форменном комбинезоне партийца. Волосы у него были совсем светлые, а румяное лицо шелушилось от скверного мыла, тупых лезвий и холода только что кончившейся зимы.\n" +
                "\n" +
                "Мир снаружи, за закрытыми окнами, дышал холодом. Ветер закручивал спиралями пыль и обрывки бумаги; и хотя светило солнце, а небо было резко-голубым, все в городе выглядело бесцветным – кроме расклеенных повсюду плакатов. С каждого заметного угла смотрело лицо черноусого. С дома напротив – тоже. СТАРШИЙ БРАТ СМОТРИТ НА ТЕБЯ – говорила подпись, и темные глаза глядели в глаза Уинстону. Внизу, над тротуаром, трепался на ветру плакат с оторванным углом, то пряча, то открывая единственное слово: АНГСОЦ. Вдалеке между крышами скользнул вертолет, завис на мгновение, как трупная муха, и по кривой унесся прочь. Это полицейский патруль заглядывал людям в окна. Но патрули в счет не шли. В счет шла только полиция мыслей.\n" +
                "\n" +
                "За спиной Уинстона голос из телекрана все еще болтал о выплавке чугуна и перевыполнении девятого трехлетнего плана. Телекран работал на прием и на передачу. Он ловил каждое слово, если его произносили не слишком тихим шепотом; мало того, покуда Уинстон оставался в поле зрения мутной пластины, он был не только слышен, но и виден. Конечно, никто не знал, наблюдают за ним в данную минуту или нет. Часто ли и по какому расписанию подключается к твоему кабелю полиция мыслей – об этом можно было только гадать. Не исключено, что следили за каждым – и круглые сутки. Во всяком случае, подключиться могли когда угодно. Приходилось жить – и ты жил, по привычке, которая превратилась в инстинкт, – с сознанием того, что каждое твое слово подслушивают и каждое твое движение, пока не погас свет, наблюдают.\n" +
                "\n" +
                "Уинстон держался к телекрану спиной. Так безопаснее; хотя – он знал это – спина тоже выдает. В километре от его окна громоздилось над чумазым городом белое здание министерства правды – место его службы. Вот он, со смутным отвращением подумал Уинстон, вот он, Лондон, главный город Взлетной полосы I, третьей по населению провинции государства Океания. Он обратился к детству – попытался вспомнить, всегда ли был таким Лондон. Всегда ли тянулись вдаль эти вереницы обветшалых домов XIX века, подпертых бревнами, с залатанными картоном окнами, лоскутными крышами, пьяными стенками палисадников? И эти прогалины от бомбежек, где вилась алебастровая пыль и кипрей карабкался по грудам обломков; и большие пустыри, где бомбы расчистили место для целой грибной семьи убогих дощатых хибарок, похожих на курятники? Но – без толку, вспомнить он не мог; ничего не осталось от детства, кроме отрывочных, ярко освещенных сцен, лишенных фона и чаще всего невразумительных.";

        //StaticLayout test1 = new StaticLayout(text, new TextView(this).getPaint(), 200, Layout.Alignment.ALIGN_NORMAL, 1f,0f, false);
        //Log.d("without_line_count", String.valueOf(test1.getLineCount()));
        //Log.d("without-height", String.valueOf(test1.getHeight()));

        SpannableStringBuilder a = new SpannableStringBuilder(text);
        a.setSpan(new RelativeSizeSpan(1.5f), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        a.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        a.setSpan(new StyleSpan(Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        StaticLayout test2 = new StaticLayout(a, new TextView(this).getPaint(), 400, Layout.Alignment.ALIGN_NORMAL, 1f,0f, false);
        Log.d("with", String.valueOf(test2.getLineBottom(1)));
        Log.d("with_line_count", String.valueOf(test2.getLineCount()));

        int line = test2.getLineForVertical(123);

        Log.d("with-height", String.valueOf(test2.getHeight()));
        Log.d("with_second_line", a.subSequence(test2.getLineStart(line), test2.getLineStart(line + 1)).toString());

*/
        SettingsManager.Initialize(this);
        RecordsManager.Initialize(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shulte_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_menu_start_training_menu_text_view :
                startActivity(new Intent(this, TrainingMenuActivity.class));
                break;
            case R.id.main_menu_start_reader_text_view :
                startActivity(new Intent(this, ReaderActivity.class));
                break;
        }
    }
}
