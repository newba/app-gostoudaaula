package br.com.gostoudaaula.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.gostoudaaula.R;
import br.com.gostoudaaula.delegate.QuestoesDelegate;
import br.com.gostoudaaula.fragment.QuestoesFragment;
import br.com.gostoudaaula.model.Aluno;
import br.com.gostoudaaula.model.Avaliacao;
import br.com.gostoudaaula.model.Respostas;
import br.com.gostoudaaula.task.RespostasTask;
import butterknife.ButterKnife;

public class QuestoesActivity extends AppCompatActivity implements QuestoesDelegate {

    private int questaoAtual;
    private Avaliacao avaliacao;
    private ArrayList<Respostas> respostas;
    private Aluno aluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_frame_layout);
        ButterKnife.bind(this);
        Intent intent = getIntent();

        if (intent.hasExtra("avaliacao")) {
            this.avaliacao = intent.getParcelableExtra("avaliacao");
        }
        if (intent.hasExtra("aluno")) {
            this.aluno = intent.getParcelableExtra("aluno");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = carregaArgumentos();
        criaFragment(bundle);
    }

    private Bundle carregaArgumentos() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("avaliacao", avaliacao);
        bundle.putInt("questao_atual", questaoAtual);
        bundle.putParcelableArrayList("respostas", respostas);
        return bundle;
    }

    private void criaFragment(Bundle bundle) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new QuestoesFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.activity_frame_layout_default, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void proximaQuestao(Avaliacao avaliacao, ArrayList<Respostas> respostas) {
        this.avaliacao = avaliacao;
        this.questaoAtual++;
        this.respostas = respostas;
        criaFragment(carregaArgumentos());
    }

    @Override
    public void enviaRespostas(List<Respostas> respostas) {
        new RespostasTask(this, avaliacao, respostas, aluno, this).execute();
    }

    @Override
    public void lidaComErro(Exception erro) {
        erro.printStackTrace();
        Toast.makeText(QuestoesActivity.this, "Ocorreu um erro durante o envio de respostas", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
            this.questaoAtual--;
        } else {
            Log.i("clicou stack", "back");
            super.onBackPressed();
            chamaListaDeAlunos();
        }
    }

    @Override
    public void avaliacaoRespondida() {
        Toast.makeText(QuestoesActivity.this, "Avaliacao respondida", Toast.LENGTH_SHORT).show();
        finish();
        chamaListaDeAlunos();
    }

    private void chamaListaDeAlunos() {
        Intent intent = new Intent(this, AlunoMainActivity.class);
        intent.putExtra("aluno", aluno);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.i("chama on save", "chamando");
        bundle.putParcelable("avaliacao", avaliacao);
        bundle.putParcelableArrayList("respostas", respostas);
        bundle.putInt("questao_atual", questaoAtual);
        bundle.putParcelable("aluno", aluno);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        Log.i("chama on restore", "chamando");
        this.avaliacao = bundle.getParcelable("avaliacao");
        this.respostas = bundle.getParcelableArrayList("respostas");
        this.questaoAtual = bundle.getInt("questao_atual");
        this.aluno = bundle.getParcelable("aluno");
    }

}

