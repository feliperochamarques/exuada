import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            Usuario usuario = new Usuario("felipe", "PH");
            Impressora impressora = new Impressora();

            if (!fazerLogin(sc, usuario)) {
                System.out.println("Número de tentativas excedido. Encerrando o programa.");
                return;
            }

            System.out.println("Login realizado com sucesso!");

            while (true) {
                mostrarMenu();
                int opcao = lerInteiro(sc, "Escolha uma opção: ");

                switch (opcao) {
                    case 1 -> impressora.configurar(sc);
                    case 2 -> impressora.abrirConexao();
                    case 3 -> executarComConexao(impressora, impressora::fecharConexao);
                    case 4 -> executarComConexao(impressora, () -> impressora.imprimirTexto(sc));
                    case 5 -> executarComConexao(impressora, () -> impressora.imprimirQRCode(sc));
                    case 6 -> executarComConexao(impressora, impressora::imprimirCodigoBarras);
                    case 7 -> executarComConexao(impressora, impressora::imprimirXMLSAT);
                    case 8 -> executarComConexao(impressora, impressora::imprimirXMLCancelamentoSAT);
                    case 9 -> executarComConexao(impressora, impressora::abrirGaveta);
                    case 10 -> executarComConexao(impressora, () -> impressora.sinalSonoro(sc));
                    case 11 -> executarComConexao(impressora, () -> impressora.avancarPapel(sc));
                    case 12 -> executarComConexao(impressora, impressora::statusImpressora);
                    case 13 -> executarComConexao(impressora, impressora::cortarPapel);
                    case 0 -> {
                        System.out.println("Programa encerrado.");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            }
        }
    }

    private static boolean fazerLogin(Scanner sc, Usuario usuario) {
        for (int tentativas = 3; tentativas > 0; tentativas--) {
            System.out.print("Nome: ");
            String login = sc.nextLine();
            System.out.print("Senha: ");
            String senha = sc.nextLine();

            if (usuario.validar(login, senha)) {
                return true;
            }

            if (tentativas > 1) {
                System.out.println("Usuário ou senha incorretos. Tentativas restantes: " + (tentativas - 1));
            }
        }
        return false;
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("---- MENU ----");
        System.out.println("1  - Configurar conexão");
        System.out.println("2  - Abrir conexão");
        System.out.println("3  - Fechar conexão");
        System.out.println("4  - Imprimir texto");
        System.out.println("5  - Imprimir QR Code");
        System.out.println("6  - Imprimir código de barras");
        System.out.println("7  - Imprimir XML SAT");
        System.out.println("8  - Imprimir XML de cancelamento SAT");
        System.out.println("9  - Abrir gaveta");
        System.out.println("10 - Sinal sonoro");
        System.out.println("11 - Avançar papel");
        System.out.println("12 - Status da impressora");
        System.out.println("13 - Cortar papel");
        System.out.println("0  - Sair");
    }

    static int lerInteiro(Scanner sc, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    private static void executarComConexao(Impressora impressora, Runnable acao) {
        if (!impressora.isConexaoAberta()) {
            System.out.println("Conexão fechada. Abra a conexão antes.");
            return;
        }
        acao.run();
    }

}