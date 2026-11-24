import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Scanner;

public class Impressora {

    private static final int POSICAO_TEXTO_FIXA = 1;
    private static final int ESTILO_TEXTO_FIXO = 4;
    private static final int TAMANHO_TEXTO_FIXO = 0;
    private static final int TAMANHO_QR_FIXO = 6;
    private static final int NIVEL_CORRECAO_QR_FIXO = 4;
    private static final int TIPO_CODIGO_BARRAS_FIXO = 8;
    private static final String CODIGO_BARRAS_FIXO = "{A012345678912";
    private static final int ALTURA_CODIGO_BARRAS_FIXA = 100;
    private static final int LARGURA_CODIGO_BARRAS_FIXA = 2;
    private static final int HRI_CODIGO_BARRAS_FIXO = 3;
    private static final int PINO_GAVETA_FIXO = 1;
    private static final int TEMPO_INICIO_GAVETA_FIXO = 5;
    private static final int TEMPO_FIM_GAVETA_FIXO = 10;
    private static final int TEMPO_INICIO_SOM_FIXO = 5;
    private static final int TEMPO_FIM_SOM_FIXO = 2;
    private static final int PARAMETRO_STATUS_FIXO = 3;

    private static final String ASSINATURA_QRCODE_CANCELAMENTO =
            "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0" +
                    "E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQ" +
                    "sE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+Z" +
                    "fO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

    private static final int AVANCO_CORTE_FIXO = 2;

    private int tipo;
    private String modelo;
    private String conexao;
    private int parametro;
    private boolean conexaoAberta;

    public Impressora() {
        tipo = 1;
        modelo = "i9";
        conexao = "USB";
        parametro = 0;
        conexaoAberta = false;
    }

    public boolean isConexaoAberta() {
        return conexaoAberta;
    }

    public void configurar(Scanner sc) {
        System.out.println("1 = USB, 2 = Serial, 3 = Rede (TCP/IP), 4 = Bluetooth");
        tipo = Main.lerInteiro(sc, "Tipo de conexão: ");
        System.out.print("Modelo (i8 ou i9): ");
        modelo = sc.nextLine();
        System.out.print("Conexão (USB, COM2, IP...): ");
        conexao = sc.nextLine();
        parametro = Main.lerInteiro(sc, "Parâmetro (0 para USB): ");
        System.out.println("Configuração salva.");
    }

    public void abrirConexao() {
        try {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso.");
            } else {
                System.out.println("Erro ao abrir conexão. Código: " + retorno);
            }
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Não foi possível carregar a DLL: " + e.getMessage());
        }
    }

    public void fecharConexao() {
        int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (retorno == 0) {
            conexaoAberta = false;
            System.out.println("Conexão fechada com sucesso.");
        } else {
            System.out.println("Erro ao fechar conexão. Código: " + retorno);
        }
    }

    public void imprimirTexto(String dados) {
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(
                dados, POSICAO_TEXTO_FIXA, ESTILO_TEXTO_FIXO, TAMANHO_TEXTO_FIXO);
        if (retorno >= 0) {
            System.out.println("Texto enviado com sucesso.");
            ImpressoraDLL.INSTANCE.AvancaPapel(2);
            cortarPapel();
        } else {
            System.out.println("Erro ao imprimir texto. Código: " + retorno);
        }
    }

    public void imprimirTexto(Scanner sc) {
        System.out.print("Texto a imprimir: ");
        imprimirTexto(sc.nextLine());
    }

    public void imprimirQRCode(String dados) {
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(
                dados, TAMANHO_QR_FIXO, NIVEL_CORRECAO_QR_FIXO);
        if (retorno == 0) {
            System.out.println("QR Code impresso com sucesso.");
            ImpressoraDLL.INSTANCE.AvancaPapel(3);
            cortarPapel();
        } else {
            System.out.println("Erro ao imprimir QR Code. Código: " + retorno);
        }
    }

    public void imprimirQRCode(Scanner sc) {
        System.out.print("Conteúdo do QR Code: ");
        imprimirQRCode(sc.nextLine());
    }

    public void imprimirCodigoBarras() {
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(
                TIPO_CODIGO_BARRAS_FIXO,
                CODIGO_BARRAS_FIXO,
                ALTURA_CODIGO_BARRAS_FIXA,
                LARGURA_CODIGO_BARRAS_FIXA,
                HRI_CODIGO_BARRAS_FIXO);
        if (retorno == 0) {
            System.out.println("Código de barras impresso com sucesso.");
            ImpressoraDLL.INSTANCE.AvancaPapel(3);
            cortarPapel();
        } else {
            System.out.println("Erro ao imprimir código de barras. Código: " + retorno);
        }
    }

    public void imprimirXMLSAT() {
        try {
            String conteudo = Files.readString(Path.of("XMLSAT.xml"));
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(conteudo, 0);
            if (retorno == 0) {
                System.out.println("Cupom fiscal (SAT) impresso com sucesso.");
                ImpressoraDLL.INSTANCE.AvancaPapel(3);
                cortarPapel();
            } else {
                System.out.println("Erro ao imprimir XML SAT. Código: " + retorno);
            }
        } catch (IOException | InvalidPathException e) {
            System.out.println("Arquivo não encontrado ou caminho inválido: XMLSAT.xml");
        }
    }

    public void imprimirXMLCancelamentoSAT() {
        try {
            String conteudo = Files.readString(Path.of("CANC_SAT.xml"));
            int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(
                    conteudo, ASSINATURA_QRCODE_CANCELAMENTO, 0);
            if (retorno == 0) {
                System.out.println("Cupom de cancelamento impresso com sucesso.");
                ImpressoraDLL.INSTANCE.AvancaPapel(3);
                cortarPapel();
            } else {
                System.out.println("Erro ao imprimir XML de cancelamento. Código: " + retorno);
            }
        } catch (IOException | InvalidPathException e) {
            System.out.println("Arquivo não encontrado ou caminho inválido: CANC_SAT.xml");
        }
    }

    public void abrirGaveta() {
        int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(
                PINO_GAVETA_FIXO, TEMPO_INICIO_GAVETA_FIXO, TEMPO_FIM_GAVETA_FIXO);
        if (retorno == 0) {
            System.out.println("Gaveta aberta com sucesso.");
        } else {
            System.out.println("Erro ao abrir a gaveta. Código: " + retorno);
        }
    }

    public void sinalSonoro(Scanner sc) {
        int quantidade = solicitarInteiro(sc, "Quantidade de bips (1 a 63): ", 1, 63);
        int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(
                quantidade, TEMPO_INICIO_SOM_FIXO, TEMPO_FIM_SOM_FIXO);
        if (retorno == 0) {
            System.out.println("Bip!");
        } else {
            System.out.println("Erro no sinal sonoro. Código: " + retorno);
        }
    }

    public void avancarPapel(int linhas) {
        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
        if (retorno == 0) {
            System.out.println("Papel avançado com sucesso.");
        } else {
            System.out.println("Erro ao avançar papel. Código: " + retorno);
        }
    }

    public void avancarPapel(Scanner sc) {
        int linhas = solicitarInteiro(sc, "Quantidade de linhas (1 a 100): ", 1, 100);
        avancarPapel(linhas);
    }

    public void cortarPapel() {
        int retorno = ImpressoraDLL.INSTANCE.Corte(AVANCO_CORTE_FIXO);
        if (retorno == 0) {
            System.out.println("Papel cortado com sucesso.");
        } else {
            System.out.println("Erro ao cortar papel. Código: " + retorno);
        }
    }

    public void statusImpressora() {
        int retorno = ImpressoraDLL.INSTANCE.StatusImpressora(PARAMETRO_STATUS_FIXO);
        if (retorno < 0) {
            System.out.println("Erro ao consultar status. Código: " + retorno);
            return;
        }
        System.out.println("Status retornado: " + retorno);
        switch (retorno) {
            case 5 -> System.out.println("Papel: tem papel.");
            case 6 -> System.out.println("Papel: acabando.");
            case 7 -> System.out.println("Papel: sem papel.");
            default -> System.out.println("Papel: status desconhecido.");
        }
    }

    private int solicitarInteiro(Scanner sc, String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                System.out.println("Valor fora do intervalo permitido.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite apenas números.");
            }
        }
    }
}