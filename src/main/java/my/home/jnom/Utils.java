package my.home.jnom;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private static FastDateFormat DATE_FORMAT =
            FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone("GMT-0"));

    private static final Pattern HSTORE_PATTERN = Pattern.compile("\"?([^\"]+)\"=>\"([^\"]+)\"?");

    public static Date parseDate(String date) {
        Date result = null;
        try {
            result = DATE_FORMAT.parse(date);
        } catch (ParseException ex) {
            LOG.warn("Wrong date format {} ", date);
        }
        return result;
    }

   /* public static String mapToHstoreString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue().replaceAll("\"", "\"\"");
            sb.append("\"").append(entry.getKey()).append("\"=>\"")
                    .append(value).append("\",");
        }

        return sb.toString();
    }

    public static Map<String, String> hstoreStringToMap(String hstore) {
        Map<String, String> result = new HashMap<>();
        hstore = hstore.replaceAll("\"\"", "\"");
        String[] pairs = hstore.split("\",[\\s]?\"");
        for (String pair : pairs) {
            Matcher matcher = HSTORE_PATTERN.matcher(pair);
            if (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                result.put(key, value);
            }
            //System.out.println(pair);
            //String[] dividedPair = pair.split("\"=>\"");
            //String key = dividedPair[0].trim();
            //String value = dividedPair[1].trim();
           // result.put(key, value);
        }

        return result;
    }

    public static void main(String[] args) {
        String string = "\"flag\"=>\"http://upload.wikimedia.org/wikipedia/commons/f/f3/Flag_of_Russia.svg\", \"name\"=>\"Россия\", \"type\"=>\"boundary\", \"int_ref\"=>\"RU\", \"name:ab\"=>\"Урыстәыла\", \"name:af\"=>\"Rusland\", \"name:ak\"=>\"Russia\", \"name:am\"=>\"ሩሲያ\", \"name:an\"=>\"Rusia\", \"name:ar\"=>\"روسيا\", \"name:as\"=>\"ৰুশিযা\", \"name:av\"=>\"Россиялъул Федерация\", \"name:ay\"=>\"Rusiya\", \"name:az\"=>\"Rusiya\", \"name:ba\"=>\"Рәсәй\", \"name:be\"=>\"Расійская Федэрацыя\", \"name:bg\"=>\"Русия\", \"name:bi\"=>\"Rusia\", \"name:bm\"=>\"Risila\", \"name:bn\"=>\"রাশিয়া\", \"name:bo\"=>\"ཨུ་རུ་སུ།\", \"name:br\"=>\"Rusia\", \"name:bs\"=>\"Rusija\", \"name:ca\"=>\"Rússia\", \"name:ce\"=>\"Росси\", \"name:ch\"=>\"Russia\", \"name:co\"=>\"Russia\", \"name:cs\"=>\"Rusko\", \"name:cu\"=>\"Рѡсїꙗ\", \"name:cv\"=>\"Раççей Патшалăхĕ\", \"name:cy\"=>\"Rwsia\", \"name:da\"=>\"Rusland\", \"name:de\"=>\"Russland\", \"name:dv\"=>\"ރޫސީވިލާތް\", \"name:dz\"=>\"ར་ཤི་ཡཱན་ཕེ་ཌི་རེ་ཤཱན\", \"name:ee\"=>\"Russia\", \"name:el\"=>\"Ρωσία\", \"name:en\"=>\"Russia\", \"name:eo\"=>\"Rusujo\", \"name:es\"=>\"Rusia\", \"name:et\"=>\"Venemaa\", \"name:eu\"=>\"Errusia\", \"name:fa\"=>\"روسیه\", \"name:ff\"=>\"Roosiya\", \"name:fi\"=>\"Venäjä\", \"name:fo\"=>\"Russland\", \"name:fr\"=>\"Fédération de Russie\", \"name:fy\"=>\"Ruslân\", \"name:ga\"=>\"An Rúis\", \"name:gd\"=>\"An Ruis\", \"name:gl\"=>\"Rusia\", \"name:gn\"=>\"Rrusia\", \"name:gu\"=>\"રશિયા\", \"name:gv\"=>\"Yn Roosh\", \"name:ha\"=>\"Rasha\", \"name:he\"=>\"רוסיה\", \"name:hi\"=>\"रूस\", \"name:hr\"=>\"Ruska Federacija\", \"name:ht\"=>\"Risi\", \"name:hu\"=>\"Oroszország\", \"name:hy\"=>\"Ռուսաստան\", \"name:ia\"=>\"Russia\", \"name:id\"=>\"Rusia\", \"name:ie\"=>\"Russia\", \"name:ig\"=>\"Mpaghara Russia\", \"name:ik\"=>\"Russia\", \"name:io\"=>\"Rusia\", \"name:is\"=>\"Rússland\", \"name:it\"=>\"Russia\", \"name:iu\"=>\"ᐅᓛᓴ\", \"name:ja\"=>\"ロシア\", \"name:jv\"=>\"Rusia\", \"name:ka\"=>\"რუსეთი\", \"name:kg\"=>\"Rusia\", \"name:ki\"=>\"Russia\", \"name:kk\"=>\"Ресей\", \"name:kl\"=>\"Ruslandi\", \"name:km\"=>\"រុស្ស៊ី\", \"name:kn\"=>\"ರಷ್ಯಾ\", \"name:ko\"=>\"러시아\", \"name:ks\"=>\"روٗس\", \"name:ku\"=>\"Rûsya\", \"name:kv\"=>\"Рочму\", \"name:kw\"=>\"Russi\", \"name:ky\"=>\"Орусия\", \"name:la\"=>\"Russia\", \"name:lb\"=>\"Russland\", \"name:lg\"=>\"Rwasha\", \"name:li\"=>\"Rusland\", \"name:ln\"=>\"Rusí\", \"name:lo\"=>\"ລັດເຊັຽ\", \"name:lt\"=>\"Rusija\", \"name:lv\"=>\"Krievija\", \"name:mg\"=>\"Rosia\", \"name:mi\"=>\"Rūhia\", \"name:mk\"=>\"Русија\", \"name:ml\"=>\"റഷ്യ\", \"name:mn\"=>\"Оросын Холбооны Улс\", \"name:mr\"=>\"रशिया\", \"name:ms\"=>\"Rusia\", \"name:mt\"=>\"Russja\", \"name:my\"=>\"ရုရှားနိုင်ငံ\", \"name:na\"=>\"Ratsiya\", \"name:ne\"=>\"रुस\", \"name:nl\"=>\"Russische Federatie\", \"name:nn\"=>\"Russland\", \"name:no\"=>\"Rusland\", \"name:nv\"=>\"Biʼééʼ Łichííʼí Bikéyah\", \"name:ny\"=>\"Russia\", \"name:oc\"=>\"Russia\", \"name:om\"=>\"Raashiyaa\", \"name:or\"=>\"ଋଷିଆ\", \"name:os\"=>\"Уæрæсе\", \"name:pa\"=>\"ਰੂਸ\", \"name:pl\"=>\"Rosja\", \"name:ps\"=>\"روسیه\", \"name:pt\"=>\"Rússia\", \"name:qu\"=>\"Rusiya\", \"name:rm\"=>\"Russia\", \"name:rn\"=>\"Uburusiya\", \"name:ro\"=>\"Rusia\", \"name:ru\"=>\"Россия\", \"name:rw\"=>\"Uburusiya\", \"name:sc\"=>\"Rùssia\", \"name:sd\"=>\"روس\", \"name:se\"=>\"Ruošša\", \"name:sg\"=>\"Rusïi\", \"name:sh\"=>\"Rusija\", \"name:si\"=>\"රුසියාව\", \"name:sk\"=>\"Rusko\", \"name:sl\"=>\"Rusija\", \"name:sm\"=>\"Lusia\", \"name:sn\"=>\"Russia\", \"name:so\"=>\"Ruushka\", \"name:sq\"=>\"Rusia\", \"name:sr\"=>\"Русија\", \"name:ss\"=>\"IRashiya\", \"name:st\"=>\"Russia\", \"name:su\"=>\"Rusia\", \"name:sv\"=>\"Ryssland\", \"name:sw\"=>\"Shirikisho la Urusi\", \"name:ta\"=>\"உருசியா\", \"name:te\"=>\"రష్యా\", \"name:tg\"=>\"Русия\", \"name:th\"=>\"ประเทศรัสเซีย\", \"name:ti\"=>\"ራሻ\", \"name:tk\"=>\"Russiýa\", \"name:tl\"=>\"Pederasyong Ruso\", \"name:to\"=>\"Lūsia\", \"name:tr\"=>\"Rusya Federasyonu\", \"name:ts\"=>\"Russia\", \"name:tt\"=>\"Русия\", \"name:tw\"=>\"Russia\", \"name:ty\"=>\"Rūtia\", \"name:ug\"=>\"روسىيە\", \"name:uk\"=>\"Росія\", \"name:ur\"=>\"روس\", \"name:uz\"=>\"Rossiya Federatsiyasi\", \"name:ve\"=>\"Rashia\", \"name:vi\"=>\"Liên bang Nga\", \"name:vo\"=>\"Rusän\", \"name:wa\"=>\"Federåcion d' Rûsseye\", \"name:wo\"=>\"Federaasioŋ bu Riisi\", \"name:xh\"=>\"IRashiya\", \"name:yi\"=>\"רוסלאנד\", \"name:yo\"=>\"Rọ́síà\", \"name:za\"=>\"Ezlozswh Lienzbangh\", \"name:zh\"=>\"俄罗斯\", \"name:zu\"=>\"IRashiya\", \"boundary\"=>\"administrative\", \"int_name\"=>\"Russia\", \"name:ace\"=>\"Rusia\", \"name:als\"=>\"Russland\", \"name:ang\"=>\"Russland\", \"name:arc\"=>\"ܪܘܣܝܐ\", \"name:arz\"=>\"روسيا\", \"name:ast\"=>\"Rusia\", \"name:bar\"=>\"Russland\", \"name:bcl\"=>\"Rusya\", \"name:bjn\"=>\"Rusia\", \"name:bpy\"=>\"রাশিয়া\", \"name:bug\"=>\"Russia\", \"name:bxr\"=>\"Ородой Холбооной Улас\", \"name:cdo\"=>\"Ngò̤-lò̤-sṳ̆\", \"name:ceb\"=>\"Rusya\", \"name:chr\"=>\"ᏲᏂᏱ\", \"name:chy\"=>\"Russia\", \"name:ckb\"=>\"ڕووسیا\", \"name:crh\"=>\"Rusiye\", \"name:csb\"=>\"Ruskô\", \"name:diq\"=>\"Rusya\", \"name:dsb\"=>\"Rusojska\", \"name:eml\"=>\"Rossia\", \"name:ext\"=>\"Russia\", \"name:frp\"=>\"Russie\", \"name:frr\"=>\"Ruslönj\", \"name:fur\"=>\"Russie\", \"name:gag\"=>\"Rusiya\", \"name:gan\"=>\"俄羅斯\", \"name:hak\"=>\"Ngò-lò-sṳ̂\", \"name:haw\"=>\"Rūsia\", \"name:hif\"=>\"Russia\", \"name:hsb\"=>\"Ruska federacija\", \"name:ilo\"=>\"Rusia\", \"name:jbo\"=>\"rukygu'e\", \"name:kaa\"=>\"Rossiya\", \"name:kab\"=>\"Rrus\", \"name:kbd\"=>\"Урысей\", \"name:koi\"=>\"Рочму\", \"name:krc\"=>\"Россия Федерация\", \"name:lad\"=>\"Rusia\", \"name:lbe\"=>\"Аьрасат\", \"name:lez\"=>\"Урусат\", \"name:lij\"=>\"Ruscia\", \"name:lmo\"=>\"Rüssia\", \"name:ltg\"=>\"Krīveja\", \"name:mdf\"=>\"Рузмастор\", \"name:mhr\"=>\"Россий\", \"name:min\"=>\"Rusia\", \"name:mrj\"=>\"Россий\", \"name:mwl\"=>\"Rússia\", \"name:myv\"=>\"Россия Мастор\", \"name:mzn\"=>\"ئوروسیا\", \"name:nah\"=>\"Rusia\", \"name:nap\"=>\"Russia\", \"name:nds\"=>\"Russ'sche Föderatschoon\", \"name:new\"=>\"रुस\", \"name:nov\"=>\"Rusia\", \"name:nrm\"=>\"Russie\", \"name:nso\"=>\"Russia\", \"name:pag\"=>\"Rusia\", \"name:pam\"=>\"Rusia\", \"name:pap\"=>\"Rusia\", \"name:pcd\"=>\"Russie\", \"name:pdc\"=>\"Russland\", \"name:pfl\"=>\"Russlond\", \"name:pih\"=>\"Rusha\", \"name:pms\"=>\"Russia\", \"name:pnb\"=>\"روس\", \"name:pnt\"=>\"Ρουσία\", \"name:rmy\"=>\"Rusiya\", \"name:rue\"=>\"Росія\", \"name:sah\"=>\"Арассыыйа\", \"name:scn\"=>\"Russia\", \"name:sco\"=>\"Roushie\", \"name:srn\"=>\"Rusland\", \"name:stq\"=>\"Ruslound\", \"name:szl\"=>\"Rusyjo\", \"name:tet\"=>\"Rúsia\", \"name:tok\"=>\"ma Losi\", \"name:tpi\"=>\"Rasia\", \"name:tum\"=>\"Russia\", \"name:tzl\"=>\"Rußía\", \"name:udm\"=>\"Россия\", \"name:vec\"=>\"Rusia\", \"name:vep\"=>\"Venäma\", \"name:vls\"=>\"Rusland\", \"name:war\"=>\"Rusya\", \"name:wuu\"=>\"俄罗斯\", \"name:xal\"=>\"Орсн Орн Нутг\", \"name:xmf\"=>\"რუსეთი\", \"name:zea\"=>\"Rusland\", \"old_name\"=>\"Русь;Золотая Орда;Русское Царство;Российская Империя;Союз Советских Социалистических Республик\", \"wikidata\"=>\"Q159\", \"ISO3166-1\"=>\"RU\", \"wikipedia\"=>\"ru:Россия\", \"name:UN:ar\"=>\"الاتحاد الروسي\", \"name:UN:en\"=>\"Russian Federation (the)\", \"name:UN:es\"=>\"Federación de Rusia (la)\", \"name:UN:fr\"=>\"Fédération de Russie (la)\", \"name:UN:ru\"=>\"Российская Федерация\", \"name:UN:zh\"=>\"俄罗斯联邦\", \"population\"=>\"143666931\", \"short_name\"=>\"РФ\", \"admin_level\"=>\"2\", \"alt_name:eo\"=>\"Rusio;Ruslando\", \"border_type\"=>\"nation\", \"name:nds-nl\"=>\"Ruslaand\", \"name:zh-yue\"=>\"俄羅斯\", \"old_name:pl\"=>\"Związek Socjalistycznych Republik Radzieckich\", \"old_name:ru\"=>\"Русь;Золотая Орда;Русское Царство;Российская Империя;Союз Советских Социалистических Республик\", \"name:bat-smg\"=>\"Rosėjė\", \"name:cbk-zam\"=>\"Rusia\", \"name:fiu-vro\"=>\"Vinnemaa\", \"name:map-bms\"=>\"Rusia\", \"name:roa-rup\"=>\"Arusia\", \"name:roa-tara\"=>\"Russie\", \"official_name\"=>\"Российская Федерация\", \"short_name:ru\"=>\"РФ\", \"name:be-tarask\"=>\"Расея\", \"old_short_name\"=>\"СССР\", \"name:zh-min-nan\"=>\"Lō͘-se-a\", \"population:date\"=>\"2014-01-01\", \"ISO3166-1:alpha2\"=>\"RU\", \"ISO3166-1:alpha3\"=>\"RUS\", \"official_name:cs\"=>\"Ruská federace\", \"official_name:de\"=>\"Russische Föderation\", \"official_name:en\"=>\"Russian Federation\", \"official_name:eo\"=>\"Rusa Federacio\", \"official_name:es\"=>\"Federación Rusa\", \"official_name:et\"=>\"Venemaa Föderatsioon\", \"official_name:fi\"=>\"Venäjän Federaatio\", \"official_name:pl\"=>\"Federacja Rosyjska\", \"official_name:pt\"=>\"Federação Russa\", \"official_name:ru\"=>\"Российская Федерация\", \"official_name:sk\"=>\"Ruská federácia\", \"official_name:sl\"=>\"Ruska federacija\", \"official_name:sv\"=>\"Ryska Federationen\", \"official_name:uk\"=>\"Російська Федерація\", \"official_name:yi\"=>\"רוסישע פֿעדעראַציע\", \"ISO3166-1:numeric\"=>\"643\", \"name:zh-classical\"=>\"俄羅斯\", \"official_name:ast\"=>\"Federación Rusa\", \"old_short_name:en\"=>\"USSR\", \"old_short_name:pl\"=>\"ZSRR\", \"old_short_name:ru\"=>\"СССР\"";
        Map<String, String> result = hstoreStringToMap(string);
        System.out.println(result);
        System.out.println(result.get("official_name:yi"));
        System.out.println(result.get("flag"));
    }
     */

   /* public static String membersToString(List<MemberEntity> members) {
        //("Bracknell", 123, "MarketSt")
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            sb.append("(\"").append(members.get(i).getType());
            sb.append("\",");
            sb.append(members.get(i).getRef());
            sb.append(",\"");
            sb.append(members.get(i).getRole());
            sb.append("\")");
            if (i < members.size()-1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    */
}
