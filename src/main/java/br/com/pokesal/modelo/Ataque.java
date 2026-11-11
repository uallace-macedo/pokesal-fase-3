package modelo;

import enums.EfeitoStatus;
import enums.TipoElemental;

/** Representa os ataques disponíveis para os pokesal */
    public class Ataque {

        private final String nome;
        private final TipoElemental tipo;
        private final int danoBase;
        private final EfeitoStatus efeitoStatus;
        private final boolean elemental;

        /**
         * Cria um ataque elemental com seus atributos.
         *
         * @param nome nome do ataque
         * @param tipo tipo elemental do ataque
         * @param danoBase dano base do ataque
         * @param efeitoStatus efeito de status aplicado pelo ataque
         */
        public Ataque(String nome, TipoElemental tipo, int danoBase, EfeitoStatus efeitoStatus) {
            this(nome, tipo, danoBase, efeitoStatus, true);
        }

        /**
         * Cria um ataque com controle sobre sua natureza elemental.
         *
         * @param nome nome do ataque
         * @param tipo tipo elemental do ataque
         * @param danoBase dano base do ataque
         * @param efeitoStatus efeito de status aplicado pelo ataque
         * @param elemental indica se o ataque recebe multiplicadores elementais e de terreno
         */
        public Ataque(
                String nome, TipoElemental tipo, int danoBase, EfeitoStatus efeitoStatus, boolean elemental) {

            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("O nome do ataque não pode ser vazio");
            }

            if (tipo == null || efeitoStatus == null) {
                throw new IllegalArgumentException("Tipo e efeito de status são obrigatórios");
            }

            if (danoBase < 0) {
                throw new IllegalArgumentException("O dano base não pode ser negativo");
            }

            this.nome = nome;
            this.tipo = tipo;
            this.danoBase = danoBase;
            this.efeitoStatus = efeitoStatus;
            this.elemental = elemental;
        }

        /**
         * @return nome do ataque
         */
        public String getNome() {
            return nome;
        }

        /**
         * @return tipo elemental do ataque
         */
        public TipoElemental getTipo() {
            return tipo;
        }

        /**
         * @return dano base do ataque
         */
        public int getDanoBase() {
            return danoBase;
        }

        /**
         * @return efeito de status do ataque
         */
        public EfeitoStatus getEfeitoStatus() {
            return efeitoStatus;
        }

        /**
         * @return true se o ataque recebe multiplicadores elementais e de terreno
         */
        public boolean isElemental() {
            return elemental;
        }
    }


