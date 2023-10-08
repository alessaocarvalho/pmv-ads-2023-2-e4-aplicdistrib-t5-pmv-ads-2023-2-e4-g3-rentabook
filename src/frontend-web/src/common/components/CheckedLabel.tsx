import * as React from 'react';
import { View, Image, Text, StyleSheet } from "react-native";
import { GreyColor, InputLabelGreenColor } from '../theme/colors';
import Assets from '../theme/assets';

/**
 * Props
 */

type CheckedLabelProps = {
  label?: string,
  width?: number,
  height?: number,
};

/**
 * Style
 */

const style = StyleSheet.create({
  container: {
    flexDirection: 'row',
    paddingVertical: 4,
    paddingHorizontal: 16,
    alignItems: 'center',
  },
  label: {
    borderRadius: 12,
    color: InputLabelGreenColor,
    fontSize: 14,
    marginBottom: 4,
    marginStart: 8,
  },
});

/**
 * CheckedLabel
 * https://www.figma.com/file/2lR8urPO212OkkhvDTmmgF/Untitled?type=design&node-id=32-254&mode=design&t=G0WN8D6m416029bq-4
 */

export default function CheckedLabel({ label, width, height }: CheckedLabelProps) {
  const [size, setSize] = React.useState<{ width?: number, height?: number }>({});

  React.useEffect(() => {
    Image.getSize(Assets.IcCheckbox, (w, h) => {
      setSize({ width: w * .6, height: h * .6 })
    });
  }, []);

  return (
    <View style={{ width, height }}>
      <View style={style.container}>
        <Image source={{ uri: Assets.IcCheckbox, width: size.width, height: size.height }} />
        <Text style={style.label}>{label}</Text>
      </View>
    </View>
  );
}