package fr.octocorn.elasticspringboot.infrastructure.datagen;

/**
 * Référentiel des métiers utilisé pour la génération de données.
 * Chaque constante porte son libellé et son secteur de rattachement.
 */
public enum Jobs {

    // Informatique (20)
    IT_DEV_FRONTEND   ("Développeur Frontend",              Sectors.IT),
    IT_DEV_BACKEND    ("Développeur Backend",               Sectors.IT),
    IT_DEV_FULLSTACK  ("Développeur Full Stack",            Sectors.IT),
    IT_DEV_MOBILE_IOS ("Développeur Mobile iOS",            Sectors.IT),
    IT_DEV_MOBILE_AND ("Développeur Mobile Android",        Sectors.IT),
    IT_DEVOPS         ("DevOps Engineer",                   Sectors.IT),
    IT_SRE            ("Site Reliability Engineer",         Sectors.IT),
    IT_DATA_ENGINEER  ("Data Engineer",                     Sectors.IT),
    IT_DATA_SCIENTIST ("Data Scientist",                    Sectors.IT),
    IT_ML_ENGINEER    ("Machine Learning Engineer",         Sectors.IT),
    IT_ARCH_SOFT      ("Architecte Logiciel",               Sectors.IT),
    IT_ARCH_CLOUD     ("Architecte Cloud",                  Sectors.IT),
    IT_QA             ("QA Engineer",                       Sectors.IT),
    IT_PO             ("Product Owner",                     Sectors.IT),
    IT_SCRUM_MASTER   ("Scrum Master",                      Sectors.IT),
    IT_TECH_LEAD      ("Tech Lead",                         Sectors.IT),
    IT_CTO            ("CTO",                               Sectors.IT),
    IT_SYSADMIN       ("Administrateur Systèmes",           Sectors.IT),
    IT_NETADMIN       ("Administrateur Réseaux",            Sectors.IT),
    IT_CYBERSEC       ("Analyste Cybersécurité",            Sectors.IT),

    // Santé (15)
    HEALTH_GP          ("Médecin Généraliste",              Sectors.HEALTH),
    HEALTH_SPECIALIST  ("Médecin Spécialiste",              Sectors.HEALTH),
    HEALTH_NURSE       ("Infirmier(ère)",                   Sectors.HEALTH),
    HEALTH_AIDE        ("Aide-soignant(e)",                 Sectors.HEALTH),
    HEALTH_PHARMACIST  ("Pharmacien(ne)",                   Sectors.HEALTH),
    HEALTH_PHYSIO      ("Kinésithérapeute",                 Sectors.HEALTH),
    HEALTH_SURGEON     ("Chirurgien(ne)",                   Sectors.HEALTH),
    HEALTH_RADIOLOGIST ("Radiologue",                       Sectors.HEALTH),
    HEALTH_MIDWIFE     ("Sage-femme",                       Sectors.HEALTH),
    HEALTH_SPEECH      ("Orthophoniste",                    Sectors.HEALTH),
    HEALTH_PSYCHOLOGIST("Psychologue",                      Sectors.HEALTH),
    HEALTH_DIETITIAN   ("Diététicien(ne)",                  Sectors.HEALTH),
    HEALTH_ERGO        ("Ergothérapeute",                   Sectors.HEALTH),
    HEALTH_RADIO_TECH  ("Manipulateur Radio",               Sectors.HEALTH),
    HEALTH_AMBULANCE   ("Ambulancier(ère)",                 Sectors.HEALTH),

    // Finance & Comptabilité (12)
    FIN_ACCOUNTANT    ("Comptable",                         Sectors.FINANCE),
    FIN_EXPERT_ACCOUNT("Expert-comptable",                  Sectors.FINANCE),
    FIN_CONTROLLER    ("Contrôleur de Gestion",             Sectors.FINANCE),
    FIN_ANALYST       ("Analyste Financier",                Sectors.FINANCE),
    FIN_AUDITOR       ("Auditeur",                          Sectors.FINANCE),
    FIN_PAYROLL       ("Gestionnaire de Paie",              Sectors.FINANCE),
    FIN_TRADER        ("Trader",                            Sectors.FINANCE),
    FIN_WEALTH_MGR    ("Gestionnaire de Patrimoine",        Sectors.FINANCE),
    FIN_ADVISOR       ("Conseiller Financier",              Sectors.FINANCE),
    FIN_RISK          ("Risk Manager",                      Sectors.FINANCE),
    FIN_CREDIT        ("Analyste Crédit",                   Sectors.FINANCE),
    FIN_TREASURER     ("Trésorier",                         Sectors.FINANCE),

    // Commerce & Vente (13)
    COM_SALES_REP    ("Commercial Terrain",                 Sectors.COMMERCE),
    COM_ACCOUNT_MGR  ("Account Manager",                    Sectors.COMMERCE),
    COM_SALES_MGR    ("Responsable Commercial",             Sectors.COMMERCE),
    COM_BIZ_DEV      ("Business Developer",                 Sectors.COMMERCE),
    COM_CATEGORY_MGR ("Category Manager",                   Sectors.COMMERCE),
    COM_BUYER        ("Acheteur",                           Sectors.COMMERCE),
    COM_SALES_ATTACH ("Attaché Commercial",                 Sectors.COMMERCE),
    COM_SALES_HEAD   ("Responsable des Ventes",             Sectors.COMMERCE),
    COM_KAM          ("Key Account Manager",                Sectors.COMMERCE),
    COM_PRODUCT_MGR  ("Chef de Produit",                    Sectors.COMMERCE),
    COM_ADVISOR      ("Vendeur Conseil",                    Sectors.COMMERCE),
    COM_BIZ_MGR      ("Chargé d'Affaires",                  Sectors.COMMERCE),
    COM_DIR          ("Directeur Commercial",               Sectors.COMMERCE),

    // Marketing & Communication (12)
    MKT_PROJECT_MGR  ("Chef de Projet Marketing",           Sectors.MARKETING),
    MKT_TRAFFIC      ("Traffic Manager",                    Sectors.MARKETING),
    MKT_COMMUNITY    ("Community Manager",                  Sectors.MARKETING),
    MKT_SEO          ("SEO Manager",                        Sectors.MARKETING),
    MKT_GROWTH       ("Growth Hacker",                      Sectors.MARKETING),
    MKT_BRAND        ("Brand Manager",                      Sectors.MARKETING),
    MKT_COMMS        ("Chargé de Communication",            Sectors.MARKETING),
    MKT_DIR          ("Directeur Marketing",                Sectors.MARKETING),
    MKT_CONTENT      ("Content Manager",                    Sectors.MARKETING),
    MKT_CRM          ("Responsable CRM",                    Sectors.MARKETING),
    MKT_PR           ("Chargé de Relations Presse",         Sectors.MARKETING),
    MKT_WEBMASTER    ("Webmaster",                          Sectors.MARKETING),

    // Ressources Humaines (10)
    HR_MANAGER    ("Responsable RH",                        Sectors.HR),
    HR_RECRUITER  ("Chargé de Recrutement",                 Sectors.HR),
    HR_ADMIN      ("Gestionnaire RH",                       Sectors.HR),
    HR_DIRECTOR   ("Directeur RH",                          Sectors.HR),
    HR_TRAINER    ("Chargé de Formation",                   Sectors.HR),
    HR_PAYROLL    ("Responsable Paie",                      Sectors.HR),
    HR_CONSULTANT ("Consultant RH",                         Sectors.HR),
    HR_SOCIAL     ("Chargé des Relations Sociales",         Sectors.HR),
    HR_HRBP       ("HRBP",                                  Sectors.HR),
    HR_CULTURE    ("Culture & Engagement Manager",          Sectors.HR),

    // Juridique (10)
    LEGAL_LAWYER    ("Avocat",                              Sectors.LEGAL),
    LEGAL_JURIST    ("Juriste d'Entreprise",                Sectors.LEGAL),
    LEGAL_NOTARY    ("Notaire",                             Sectors.LEGAL),
    LEGAL_BAILIFF   ("Huissier de Justice",                 Sectors.LEGAL),
    LEGAL_SOCIAL    ("Juriste Social",                      Sectors.LEGAL),
    LEGAL_COMPLIANCE("Compliance Officer",                  Sectors.LEGAL),
    LEGAL_IP        ("Juriste Propriété Intellectuelle",    Sectors.LEGAL),
    LEGAL_CONTRACTS ("Juriste Contrats",                    Sectors.LEGAL),
    LEGAL_PARALEGAL ("Paralegal",                           Sectors.LEGAL),
    LEGAL_DIRECTOR  ("Directeur Juridique",                 Sectors.LEGAL),

    // BTP & Architecture (13)
    BTP_ARCHITECT   ("Architecte",                          Sectors.BTP),
    BTP_SITE_MGR    ("Conducteur de Travaux",               Sectors.BTP),
    BTP_ENGINEER    ("Ingénieur BTP",                       Sectors.BTP),
    BTP_FOREMAN     ("Chef de Chantier",                    Sectors.BTP),
    BTP_MASON       ("Maçon",                               Sectors.BTP),
    BTP_ELECTRICIAN ("Électricien",                         Sectors.BTP),
    BTP_PLUMBER     ("Plombier",                            Sectors.BTP),
    BTP_CARPENTER   ("Menuisier",                           Sectors.BTP),
    BTP_FRAMEWORK   ("Charpentier",                         Sectors.BTP),
    BTP_STUDY_OFFICE("Bureau d'Études",                     Sectors.BTP),
    BTP_SURVEYOR    ("Géomètre",                            Sectors.BTP),
    BTP_MAINTENANCE ("Technicien de Maintenance",           Sectors.BTP),
    BTP_SAFETY      ("Responsable Sécurité Chantier",       Sectors.BTP),

    // Logistique & Transport (12)
    LOG_MANAGER     ("Responsable Logistique",              Sectors.LOGISTICS),
    LOG_SUPPLY_CHAIN("Supply Chain Manager",                Sectors.LOGISTICS),
    LOG_WAREHOUSE   ("Responsable Entrepôt",                Sectors.LOGISTICS),
    LOG_DRIVER      ("Chauffeur Livreur",                   Sectors.LOGISTICS),
    LOG_TRANSIT     ("Agent de Transit",                    Sectors.LOGISTICS),
    LOG_COORDINATOR ("Coordinateur Logistique",             Sectors.LOGISTICS),
    LOG_BUYER       ("Acheteur Logistique",                 Sectors.LOGISTICS),
    LOG_TRANSPORT   ("Responsable Transport",               Sectors.LOGISTICS),
    LOG_PICKER      ("Préparateur de Commandes",            Sectors.LOGISTICS),
    LOG_DELIVERY    ("Livreur",                             Sectors.LOGISTICS),
    LOG_SHIPPING    ("Responsable Expéditions",             Sectors.LOGISTICS),
    LOG_STOCK       ("Gestionnaire de Stocks",              Sectors.LOGISTICS),

    // Éducation & Formation (10)
    EDU_TEACHER     ("Enseignant",                          Sectors.EDUCATION),
    EDU_TRAINER     ("Formateur",                           Sectors.EDUCATION),
    EDU_DIRECTOR    ("Directeur d'Établissement",           Sectors.EDUCATION),
    EDU_COUNSELOR   ("Conseiller d'Orientation",            Sectors.EDUCATION),
    EDU_LIBRARIAN   ("Documentaliste",                      Sectors.EDUCATION),
    EDU_SPECIALIST  ("Éducateur Spécialisé",                Sectors.EDUCATION),
    EDU_ANIMATOR    ("Animateur Pédagogique",               Sectors.EDUCATION),
    EDU_PRIMARY     ("Professeur des Écoles",               Sectors.EDUCATION),
    EDU_LECTURER    ("Chargé d'Enseignement",               Sectors.EDUCATION),
    EDU_INSTR_DESIGN("Ingénieur Pédagogique",               Sectors.EDUCATION),

    // Industrie & Production (12)
    IND_PROD_ENGINEER("Ingénieur de Production",            Sectors.INDUSTRY),
    IND_TECH_FAB     ("Technicien de Fabrication",          Sectors.INDUSTRY),
    IND_OPERATOR     ("Opérateur de Production",            Sectors.INDUSTRY),
    IND_QUALITY_ENG  ("Ingénieur Qualité",                  Sectors.INDUSTRY),
    IND_MAINTENANCE  ("Responsable Maintenance",            Sectors.INDUSTRY),
    IND_METHODS      ("Technicien Méthodes",                Sectors.INDUSTRY),
    IND_PROCESS      ("Ingénieur Process",                  Sectors.INDUSTRY),
    IND_PROD_MGR     ("Responsable Production",             Sectors.INDUSTRY),
    IND_QC_TECH      ("Technicien Contrôle Qualité",        Sectors.INDUSTRY),
    IND_RD_ENGINEER  ("Ingénieur R&D",                      Sectors.INDUSTRY),
    IND_HSE          ("Technicien HSE",                     Sectors.INDUSTRY),
    IND_PLANT_MGR    ("Responsable Usine",                  Sectors.INDUSTRY),

    // Restauration & Hôtellerie (10)
    HOS_COOK        ("Cuisinier",                           Sectors.HOSPITALITY),
    HOS_HEAD_CHEF   ("Chef Cuisinier",                      Sectors.HOSPITALITY),
    HOS_WAITER      ("Serveur",                             Sectors.HOSPITALITY),
    HOS_MAITRE_D    ("Maître d'Hôtel",                      Sectors.HOSPITALITY),
    HOS_RECEPTIONIST("Réceptionniste",                      Sectors.HOSPITALITY),
    HOS_ROOM_MGR    ("Responsable de Salle",                Sectors.HOSPITALITY),
    HOS_BARMAN      ("Barman",                              Sectors.HOSPITALITY),
    HOS_PASTRY      ("Pâtissier",                           Sectors.HOSPITALITY),
    HOS_HOTEL_DIR   ("Directeur d'Hôtel",                   Sectors.HOSPITALITY),
    HOS_CATERING_MGR("Responsable Restauration",            Sectors.HOSPITALITY);

    private final String label;
    private final Sectors sector;

    Jobs(String label, Sectors sector) {
        this.label = label;
        this.sector = sector;
    }

    public String getLabel() {
        return label;
    }

    public Sectors getSector() {
        return sector;
    }
}
