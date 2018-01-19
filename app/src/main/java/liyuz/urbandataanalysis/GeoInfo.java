package liyuz.urbandataanalysis;

import android.util.Log;

import java.util.HashMap;

public class GeoInfo {
    static final String[] states=
            {"Australian Capital Territory","New South Wales","Northern Territory",
                    "Queensland","South Australia","Tasmania","Victoria","Western Australia"};

    static final String[] act={"Australian Capital Territory"};

    static final String[] nsw={"Greater Sydney","Capital Region","Central Coast",
            "Central West","Coffs Harbour","Far West and Orana","Hunter Valley exc Newcastle",
            "Illawarra","Mid North Coast","Murray","Newcastle and Lake Macquarie",
            "New England and North West","Riverina","Southern Highlands and Shoalhaven",
            "Sydney - Baulkham Hills and Hawkesbury","Sydney - Blacktown","Sydney - City and Inner South",
            "Sydney - Eastern Suburbs","Sydney - Inner South West","Sydney - Inner West",
            "Sydney - Northern Beaches","Sydney - North Sydney and Hornsby",
            "Sydney - Outer South West","Sydney - Outer West and Blue Mountains","Sydney - Parramatta",
            "Sydney - Ryde","Sydney - South West","Sydney - Sutherland"};

    static final String[] nt={"Greater Darwin","Northern Territory - Outback"};

    static final String[] qld={"Greater Brisbane","Brisbane - East","Brisbane Inner City",
            "Brisbane - North","Brisbane - South","Brisbane - West","Cairns","Darling Downs - Maranoa",
            "Fitzroy","Gold Coast","Ipswich","Logan - Beaudesert","Mackay","Moreton Bay - North",
            "Moreton Bay - South","Queensland - Outback","Sunshine Coast","Toowoomba","Townsville",
            "Wide Bay"};

    static final String[] sau={"Greater Adelaide","Adelaide - Central and Hills",
            "Adelaide - North","Adelaide - South","Adelaide - West","Barossa - Yorke - Mid North",
            "South Australia - Outback","South Australia - South East"};

    static final String[] tas={"Greater Hobart","Launceston and North East","South East",
            "West and North West"};

    static final String[] vic={"Greater Melbourne","Melbourne Inner","Melbourne Inner East",
            "Melbourne Inner South","Melbourne North East","Melbourne North West","Melbourne Outer East",
            "Melbourne South East","Melbourne West","Ballarat","Bendigo","Geelong","Hume","Shepparton",
            "North West", "Mornington Peninsula","Warrnambool and South West","Latrobe Gippsland"};

    static final String[] wau={"Greater Perth","Bunbury","Mandurah","Perth - Inner",
            "Perth - North East","Perth - North West","Perth - South East","Perth - South West",
            "Western Australia - Outback","Western Australia - Wheat Belt"};

    public static HashMap<String,BBox> cityBBox = new HashMap<>();

    static {
        cityBBox.put("Greater Sydney", new BBox(149.9719, 151.6305, -34.3312, -32.9961));
        cityBBox.put("Capital Region", new BBox(147.7108, 150.3791, -37.5051, -33.8878));
        cityBBox.put("Central Coast", new BBox(150.9841, 151.6305, -33.5706, -33.0436));
        cityBBox.put("Central West", new BBox(146.0540, 150.6198, -34.3172, -31.7787));
        cityBBox.put("Coffs Harbour", new BBox(152.1683,153.3948,-30.5671,-28.9700));
        cityBBox.put("Far West and Orana", new BBox(140.9995,150.1106,-33.3667,-28.9978));
        cityBBox.put("Hunter Valley exc Newcastle", new BBox(149.7916,152.3369,-33.1390,-31.5537));
        cityBBox.put("Illawarra", new BBox(150.5275,151.0662,-34.7922,-34.0934));
        cityBBox.put("Mid North Coast", new BBox(151.4028,159.1092,-32.6158,-30.5006));
        cityBBox.put("Murray", new BBox(141.0017,147.8192,-36.1299,-32.7485));
        cityBBox.put("Newcastle and Lake Macquarie", new BBox(151.3315,151.8788,-33.2028,-32.7798));
        cityBBox.put("New England and North West", new BBox(148.6762,152.6314,-31.8582,-28.2492));
        cityBBox.put("Riverina", new BBox(144.8511,148.8088,-36.8061,-32.6713));
        cityBBox.put("Southern Highlands and Shoalhaven", new BBox(149.9761,150.8498,-35.5684,-34.2125));
        cityBBox.put("Sydney - Baulkham Hills and Hawkesbury", new BBox(150.3567,-33.7730,151.1556,-32.9961));
        cityBBox.put("Sydney - Blacktown", new BBox(150.7596,150.9669,-33.8340,-33.6429));
        cityBBox.put("Sydney - City and Inner South", new BBox(151.1366,151.2367,-33.9848,-33.8509));
        cityBBox.put("Sydney - Eastern Suburbs", new BBox(151.2112,151.2878,-34.0018,-33.8325));
        cityBBox.put("Sydney - Inner South West", new BBox(151.1366,151.2367,-33.9848,-33.8509));
        cityBBox.put("Sydney - Inner West", new BBox(151.0567,151.1966,-33.9163,-33.8228));
        cityBBox.put("Sydney - Northern Beaches", new BBox(151.1608,151.3430,-33.8239,-33.5719));
        cityBBox.put("Sydney - North Sydney and Hornsby", new BBox(151.0579,151.2689,-33.8536,-33.5073));
        cityBBox.put("Sydney - Outer South West", new BBox(150.4183,150.9967,-34.3312,-33.9408));
        cityBBox.put("Sydney - Outer West and Blue Mountains", new BBox(149.9719,150.8412,-34.3083,-33.4857));
        cityBBox.put("Sydney - Parramatta", new BBox(150.9124,151.0841,-33.8990,-33.7571));
        cityBBox.put("Sydney - Ryde", new BBox(151.0375,151.1803,-33.8449,-33.7192));
        cityBBox.put("Sydney - South West", new BBox(150.6179,150.9981,-34.0510,-33.8207));
        cityBBox.put("Sydney - Sutherland", new BBox(150.9366,151.2319,-34.1723,-33.9772));
        cityBBox.put("Greater Darwin", new BBox(130.8151, 131.3967, -12.8619, -12.0010));
        cityBBox.put("Northern Territory - Outback", new BBox(129.0004,138.0012,-25.9995,-10.9659));
        cityBBox.put("Greater Brisbane", new BBox(152.0734, 153.5467, -28.3640, -26.4519));
        cityBBox.put("Brisbane - East", new BBox(153.0879,153.5467,-27.7408,-27.0220));
        cityBBox.put("Brisbane Inner City", new BBox(152.9583,153.0896,-27.4938,-27.4040));
        cityBBox.put("Brisbane - North", new BBox(152.9752,153.1605,-27.4457,-27.2787));
        cityBBox.put("Brisbane - South", new BBox(152.9693,153.1918,-27.6604,-27.4576));
        cityBBox.put("Brisbane - West", new BBox(152.7986,153.0206,-27.6021,-27.3933));
        cityBBox.put("Cairns", new BBox(144.7408,146.3587,-18.5783,-15.9028));
        cityBBox.put("Darling Downs - Maranoa", new BBox(146.8631,152.4926,-29.1779,-24.8789));
        cityBBox.put("Fitzroy", new BBox(146.5725,152.7184,-25.9661,-21.9152));
        cityBBox.put("Gold Coast", new BBox(153.0079,153.5522,-28.3579,-27.6918));
        cityBBox.put("Ipswich", new BBox(152.1340,152.9984,-28.3390,-26.9902));
        cityBBox.put("Logan - Beaudesert", new BBox(152.6478,153.2905,-28.3640,-27.5873));
        cityBBox.put("Mackay", new BBox(146.0315,150.4420,-23.5560,-19.7055));
        cityBBox.put("Moreton Bay - North", new BBox(152.0734,153.2076,-27.2635,-26.4519));
        cityBBox.put("Moreton Bay - South", new BBox(152.6810,153.0779,-27.4224,-27.0862));
        cityBBox.put("Queensland - Outback", new BBox(137.9960,147.9553,-28.9991,-9.1422));
        cityBBox.put("Sunshine Coast", new BBox(152.5509,153.1514,-26.9848,-26.1371));
        cityBBox.put("Toowoomba", new BBox(151.7665,152.3823,-27.9697,-27.3493));
        cityBBox.put("Townsville", new BBox(144.2852,147.6633,-22.1048,-18.3137));
        cityBBox.put("Wide Bay", new BBox(150.3696,153.3604,-26.9479,-24.3921));
        cityBBox.put("Greater Adelaide", new BBox(138.4362, 139.0440, -35.3503, -34.5002));
        cityBBox.put("Adelaide - Central and Hills", new BBox(138.5719,139.0440,-35.2433,-34.6805));
        cityBBox.put("Adelaide - North", new BBox(138.4362,138.8480,-34.8883,-34.5002));
        cityBBox.put("Adelaide - South", new BBox(138.4421,138.7134,-35.3503,-34.9585));
        cityBBox.put("Adelaide - West", new BBox(138.4757,138.5879,-34.9759,-34.7552));
        cityBBox.put("Barossa - Yorke - Mid North", new BBox(136.4414,139.3580,-35.3782,-32.1203));
        cityBBox.put("South Australia - Outback", new BBox(129.0013,141.0030,-35.3404,-25.9961));
        cityBBox.put("South Australia - South East", new BBox(136.5329,140.9739,-38.0626,-33.8008));
        cityBBox.put("Greater Hobart", new BBox(147.0267, 147.9369, -43.1213, -42.6554));
        cityBBox.put("Launceston and North East", new BBox(145.9513,148.4987,-42.2813,-39.2037));
        cityBBox.put("South East", new BBox(145.8325,148.3593,-43.7405,-41.7003));
        cityBBox.put("West and North West", new BBox(143.8189,146.7621,-43.3226,-39.5793));
        cityBBox.put("Greater Melbourne", new BBox(144.9514, 144.9901, -37.8555, -37.7997));
        cityBBox.put("Ballarat", new BBox(143.0630,144.4906,-37.9885,-36.6988));
        cityBBox.put("Bendigo", new BBox(143.3152,144.8536,-37.4589,-35.9059));
        cityBBox.put("Geelong", new BBox(143.6221,-144.7202,-38.5794,-37.7812));
        cityBBox.put("Hume", new BBox(144.5304,148.2207,-37.8280,-35.9285));
        cityBBox.put("Melbourne Inner", new BBox(144.8889,145.0453,-37.8917,-37.7325));
        cityBBox.put("Melbourne Inner East", new BBox(144.9993,145.1841,-37.8759,-37.7339));
        cityBBox.put("Melbourne Inner South", new BBox(144.9834,145.1563,-38.0850,-37.8374));
        cityBBox.put("Melbourne North East", new BBox(144.8807,145.5800,-37.7851,-37.2629));
        cityBBox.put("Melbourne North West", new BBox(144.4577,144.9853,-37.7761,-37.1751));
        cityBBox.put("Melbourne Outer East", new BBox(145.1569,145.8784,-37.9750,-37.5260));
        cityBBox.put("Melbourne South East", new BBox(145.0795,145.7651,-38.3325,-37.8533));
        cityBBox.put("Melbourne West", new BBox(144.3336,144.9165,-38.0046,-37.5464));
        cityBBox.put("Shepparton", new BBox(144.2593,146.2465,-36.7626,-35.8020));
        cityBBox.put("North West", new BBox(140.9617,144.4182,-37.8366,-33.9804));
        cityBBox.put("Mornington Peninsula", new BBox(144.6514,145.2617,-38.5030,-38.0674));
        cityBBox.put("Warrnambool and South West", new BBox(140.9657,143.9461,-38.8577,-37.0870));
        cityBBox.put("Latrobe Gippsland", new BBox(145.1094,149.9767,-39.1592,-36.6124));
        cityBBox.put("Greater Perth", new BBox(115.4495, 116.4151, -32.8019, -31.4551));
        cityBBox.put("Bunbury", new BBox(114.9746,116.8568,-35.0689,-32.7552));
        cityBBox.put("Mandurah", new BBox(115.6068,116.0315,-32.8019,-32.4446));
        cityBBox.put("Perth - Inner", new BBox(115.7500,115.8934,-32.0251,-31.9075));
        cityBBox.put("Perth - North East", new BBox(115.8769,116.4151,-32.0626,-31.5972));
        cityBBox.put("Perth - North West", new BBox(115.5607,115.8999,-31.9330,-31.4551));
        cityBBox.put("Perth - South East", new BBox(115.8266,116.3581,-32.4775,-31.9159));
        cityBBox.put("Perth - South West", new BBox(115.4495,115.9162,-32.4582,-31.9873));
        cityBBox.put("Western Australia - Outback", new BBox(112.9211,129.0019,-34.4747,-13.6895));
        cityBBox.put("Western Australia - Wheat Belt", new BBox(114.9704,120.5815,-35.1348,-29.6123));
        cityBBox.put("Australian Capital Territory", new BBox(148.7627, 149.3993, -35.9208, -35.1245));
    }

    static String[] getCities(String state) {
        switch(state) {
            case "Australian Capital Territory":
                return GeoInfo.act;
            case "New South Wales":
                return GeoInfo.nsw;
            case "Northern Territory":
                return GeoInfo.nt;
            case "Queensland":
                return GeoInfo.qld;
            case "South Australia":
                return GeoInfo.sau;
            case "Tasmania":
                return GeoInfo.tas;
            case "Victoria":
                return GeoInfo.vic;
            case "Western Australia":
                return GeoInfo.wau;
            default:
                Log.i("GeoInfo", "Something wrong in getCities");
                return  GeoInfo.act;
        }
    }
}
