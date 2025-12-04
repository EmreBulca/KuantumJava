import java.util.ArrayList;
import java.util.Scanner;

// 1. ÖZEL HATA YÖNETİMİ (CUSTOM EXCEPTION)
// Sistem çöktüğünde fırlatılacak özel hata sınıfı.
// Runtime hatası yerine kendi kontrolümüzdeki bu hatayı kullanıyoruz.
class KuantumCokusuException extends Exception {
    public KuantumCokusuException(String message) {
        super(message);
    }
}

// 2. ARAYÜZ (INTERFACE SEGREGATION)
// Her nesne soğutulamaz. Sadece tehlikeli olanlar (Karanlık Madde vb.)
// bu yeteneğe sahip olmalı. Bu yüzden IKritik arayüzünü oluşturduk.
interface IKritik {
    void AcilDurumSogutmasi(); // Bu metodu uygulayan sınıf, içini doldurmak zorundadır.
}

// 3. ANA SOYUT SINIF (ABSTRACT CLASS)
// Tüm nesnelerin atası. Ortak özellikleri burada topladık.
abstract class KuantumNesnesi {
    private String id;
    private double stabilite;
    private int tehlikeSeviyesi;

    // Java'da C#'taki gibi Property olmadığı için Getter/Setter metodları
    // kullanıyoruz.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getStabilite() {
        return stabilite;
    }

    // Encapsulation (Kapsülleme) ve Patlama Kontrolü
    // Stabilite değerini doğrudan değiştirmek yerine bu metodla kontrol altında
    // tutuyoruz.
    public void setStabilite(double stabilite) throws KuantumCokusuException {
        // 100'den büyük olamaz
        if (stabilite > 100) {
            this.stabilite = 100;
        }
        // 0 veya altına düşerse OYUN BİTER (Exception fırlatılır).
        else if (stabilite <= 0) {
            this.stabilite = 0;
            // Hata fırlatılınca kodun akışı durur ve 'catch' bloğuna gider.
            throw new KuantumCokusuException(this.id + " isimli nesne kararsizlasti ve PATLADI!");
        } else {
            this.stabilite = stabilite;
        }
    }

    public int getTehlikeSeviyesi() {
        return tehlikeSeviyesi;
    }

    public void setTehlikeSeviyesi(int tehlikeSeviyesi) {
        this.tehlikeSeviyesi = tehlikeSeviyesi;
    }

    // Soyut Metot: Alt sınıflar bunu kendine göre doldurmak (Override) zorundadır.
    public abstract void AnalizEt() throws KuantumCokusuException;

    // Ortak Metot: Nesnenin durumu raporlanır.
    public String DurumBilgisi() {
        return "ID: " + id + " | Stabilite: %" + stabilite + " | Tehlike: " + tehlikeSeviyesi;
    }
}

// 4. NESNE ÇEŞİTLERİ (INHERITANCE & POLYMORPHISM)
// A. Veri Paketi (Tehlikesiz)
// IKritik DEĞİLDİR, soğutulamaz.
class VeriPaketi extends KuantumNesnesi {
    @Override
    public void AnalizEt() throws KuantumCokusuException {
        // Stabilite sadece 5 birim düşer.
        setStabilite(getStabilite() - 5);
        System.out.println("Veri icerigi okundu.");
    }
}

// B. Karanlık Madde (Tehlikeli)
// IKritik arayüzünü uygular (implements).
class KaranlikMadde extends KuantumNesnesi implements IKritik {
    @Override
    public void AnalizEt() throws KuantumCokusuException {
        // Stabilite 15 birim düşer.
        setStabilite(getStabilite() - 15);
    }

    // Interface'den gelen zorunlu metot
    @Override
    public void AcilDurumSogutmasi() {
        try {
            // +50 artar (Max 100 olacak şekilde)
            double yeniDeger = getStabilite() + 50;
            if (yeniDeger > 100)
                yeniDeger = 100;

            setStabilite(yeniDeger); // setStabilite hata fırlatabilir diye try-catch içinde çağırdık
            System.out.println(getId() + " sogutuldu. Yeni Stabilite: " + getStabilite());
        } catch (KuantumCokusuException e) {
            // Soğuturken patlama ihtimali yok ama Java kuralı gereği yazdık.
        }
    }
}

// C. Anti Madde (Çok Tehlikeli)
// IKritik arayüzünü uygular.
class AntiMadde extends KuantumNesnesi implements IKritik {
    @Override
    public void AnalizEt() throws KuantumCokusuException {
        // Stabilite çok hızlı düşer (25 birim).
        setStabilite(getStabilite() - 25);
        System.out.println("Evrenin dokusu titriyor..."); // Uyarı mesajı
    }

    @Override
    public void AcilDurumSogutmasi() {
        try {
            double yeniDeger = getStabilite() + 50;
            if (yeniDeger > 100)
                yeniDeger = 100;
            setStabilite(yeniDeger);
            System.out.println(getId() + " icin kritik sogutma uygulandi.");
        } catch (KuantumCokusuException e) {
        }
    }
}

// 5. OYUN DÖNGÜSÜ (MAIN PROGRAM)
public class Main {
    // Scanner'ı global yaptık ki tüm metotlardan erişelim.
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Generic List Kullanımı (Polimorfizm için önemli)
        ArrayList<KuantumNesnesi> envanter = new ArrayList<>();

        System.out.println("SİSTEM BAŞLATILIYOR (JAVA)...");

        // Sonsuz Döngü (Main Loop)
        while (true) {
            System.out.println("\n=== OMEGA SEKTÖRÜ (JAVA VERSİYON) ===");
            System.out.println("1. Yeni Nesne Ekle");
            System.out.println("2. Tum Envanteri Listele");
            System.out.println("3. Nesneyi Analiz Et");
            System.out.println("4. Acil Durum Sogutmasi Yap");
            System.out.println("5. Cikis");
            System.out.print("Seciminiz: ");
            String secim = scanner.nextLine();

            // Exception Handling (Hata Yakalama)
            try {
                switch (secim) {
                    case "1":
                        EklemeMenusu(envanter);
                        break;
                    case "2":
                        ListelemeMenusu(envanter);
                        break;
                    case "3":
                        AnalizMenusu(envanter);
                        break;
                    case "4":
                        SogutmaMenusu(envanter);
                        break;
                    case "5":
                        System.out.println("Cikis yapiliyor...");
                        return; // Programdan çıkar
                    default:
                        System.out.println("Gecersiz secim!");
                }
            }
            // Game Over Yakalama
            // Eğer herhangi bir nesne patlarsa buraya düşeriz.
            catch (KuantumCokusuException ex) {
                System.out.println("\n************************************************");
                System.out.println("SISTEM COKTU! TAHLİYE BASLATILIYOR...");
                System.out.println("SEBEP: " + ex.getMessage());
                System.out.println("************************************************\n");
                break; // Döngüyü kırar ve program sonlanır.
            } catch (Exception e) {
                System.out.println("Beklenmedik Hata: " + e.getMessage());
            }
        }
    }

    // --- YARDIMCI METOTLAR (Kodun okunabilirliği için) ---

    static void EklemeMenusu(ArrayList<KuantumNesnesi> envanter) {
        System.out.println("\nTip Sec: (1) Veri, (2) Karanlik Madde, (3) Anti Madde");
        String tip = scanner.nextLine();
        System.out.print("Nesne ID giriniz: ");
        String id = scanner.nextLine();

        KuantumNesnesi yeniNesne = null;

        try {
            // Factory Pattern benzeri üretim
            if (tip.equals("1")) {
                yeniNesne = new VeriPaketi();
                yeniNesne.setTehlikeSeviyesi(1);
            } else if (tip.equals("2")) {
                yeniNesne = new KaranlikMadde();
                yeniNesne.setTehlikeSeviyesi(5);
            } else if (tip.equals("3")) {
                yeniNesne = new AntiMadde();
                yeniNesne.setTehlikeSeviyesi(10);
            }

            if (yeniNesne != null) {
                yeniNesne.setId(id);
                yeniNesne.setStabilite(100); // Başlangıç full
                envanter.add(yeniNesne);
                System.out.println(id + " eklendi.");
            } else {
                System.out.println("Gecersiz tip.");
            }
        } catch (KuantumCokusuException e) {
            // Başlangıçta patlama olmaz ama try-catch zorunluluğu var.
        }
    }

    static void ListelemeMenusu(ArrayList<KuantumNesnesi> envanter) {
        System.out.println("\n--- Envanter Raporu ---");
        // Polimorfizm ile listeleme
        // Hepsi KuantumNesnesi olduğu için aynı döngüde dönüyoruz.
        for (KuantumNesnesi nesne : envanter) {
            System.out.println(nesne.DurumBilgisi());
        }
    }

    static void AnalizMenusu(ArrayList<KuantumNesnesi> envanter) throws KuantumCokusuException {
        System.out.print("\nAnaliz edilecek ID: ");
        String id = scanner.nextLine();

        KuantumNesnesi bulunan = null;
        for (KuantumNesnesi n : envanter) {
            if (n.getId().equals(id)) {
                bulunan = n;
                break;
            }
        }

        if (bulunan != null) {
            // DİKKAT: Bu metod çalışırken stabilite düşer.
            // 0'ın altına düşerse hata (Exception) fırlatır.
            bulunan.AnalizEt();
            System.out.println("Guncel Stabilite: %" + bulunan.getStabilite());
        } else {
            System.out.println("Nesne bulunamadi!");
        }
    }

    static void SogutmaMenusu(ArrayList<KuantumNesnesi> envanter) {
        System.out.print("\nSogutulacak ID: ");
        String id = scanner.nextLine();

        KuantumNesnesi bulunan = null;
        for (KuantumNesnesi n : envanter) {
            if (n.getId().equals(id)) {
                bulunan = n;
                break;
            }
        }

        // Type Checking (Tür Kontrolü)
        // Nesnenin IKritik olup olmadığını 'instanceof' ile kontrol ediyoruz.
        if (bulunan instanceof IKritik) {
            ((IKritik) bulunan).AcilDurumSogutmasi(); // Kritikse soğut.
        } else if (bulunan != null) {
            // Kritik değilse hata ver.
            System.out.println("HATA: Bu nesne sogutulamaz! (IKritik degil)");
        } else {
            System.out.println("Nesne bulunamadi.");
        }
    }
}