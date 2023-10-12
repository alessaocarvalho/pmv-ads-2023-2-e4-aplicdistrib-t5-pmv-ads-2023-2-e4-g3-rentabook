import * as React from "react";
import {
  View,
  Text,
  StyleSheet,
  Image,
  ImageSourcePropType,
} from "react-native";
import { GreenLight, PrimaryGreenColor, WhiteColor } from "../theme/colors";

const Icone: ImageSourcePropType = require("../assets/icon.png");
const IconeDois: ImageSourcePropType = require("../assets/IconDois.png");

const style = StyleSheet.create({
  navbar: {
    backgroundColor: GreenLight,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 15,
    marginBottom: 30,
  },
  navLinkContainer: {
    flexDirection: "row",
    alignItems: "center",
  },
  navLinks: {
    flexDirection: "row",
    alignItems: "center",
    marginRight: 30,
  },
  navLink: {
    color: PrimaryGreenColor,
    fontSize: 16,
    marginLeft: 16,
    fontWeight: "bold",
  },
  icon: {
    width: 54,
    height: 54,
  },
});

export default function NavBar() {
  const handleLinkClick = (text: string) => {
    console.log(`Link clicado: ${text}`);
  };

  return (
    <View style={style.navbar}>
      <Image source={Icone} style={style.icon} />
      <View style={style.navLinkContainer}>
        <View style={style.navLinks}>
          <Text
            style={style.navLink}
            onPress={() => handleLinkClick("Anúncio")}
          >
            Anúncio
          </Text>
          <Text
            style={style.navLink}
            onPress={() => handleLinkClick("Meus Anúncios")}
          >
            Meus Anúncios
          </Text>
          <Text style={style.navLink} onPress={() => handleLinkClick("Chat")}>
            Chat
          </Text>
        </View>
        <Image source={IconeDois} style={style.icon} />
      </View>
    </View>
  );
}
