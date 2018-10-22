import React, { Component } from 'react';
import {
  View,
  Button,
  ScrollView,
  StyleSheet,
} from 'react-native';
import InputField from './components/InputField';

import { NativeModules } from "react-native";
const RNWaveformAudioLib = NativeModules.RNWaveformAudioLib;

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  inputRowContainer: {
    flexDirection: 'row',
  },
  scrollViewStyle: {
            // 背景色
            //backgroundColor:'red'
        },
  itemStyle: {
            // 尺寸
            width:1000,
            height:200
   },
});

export default class SendPulseSequence extends Component {
  state = {
    firstPulseSpaceLeft: '1', // NOTE: 1 = pulse, 0 = space
    firstPulseSpaceRight: '0', // NOTE: 1 = pulse, 0 = space
    peakToPeakAmplitudeLeft: '25', // NOTE: 0 ~ 25, step = 1
    peakToPeakAmplitudeRight: '0', // NOTE: 0 ~ 25, step = 1
    pulseSpaceTimeDurationLeft: ['55'], // NOTE: each item should be a number range 1 ~ 999
    pulseSpaceTimeDurationRight: ['77'], // NOTE: each item should be a number range 1 ~ 999
  };

  updateState = (key, value) => {
    this.setState({
      [key]: value,
    });
  };

  updateStringToArrayState = (key, value) => {
    this.setState({
      [key]: value.split(',').map(i => i.trim()).filter(i => i && Number.isInteger(Number(i))),
    });
  };

  handleSend = () => {
    console.log(this.state);

    let nFpsi =  new Array();
    let nAmp_pp = new Array();
    let nPstd =  new Array();
    nFpsi.push(parseInt(this.state.firstPulseSpaceLeft), parseInt(this.state.firstPulseSpaceRight));
    nAmp_pp.push(parseInt(this.state.peakToPeakAmplitudeLeft), parseInt(this.state.peakToPeakAmplitudeRight));
    let leftArray = this.state.pulseSpaceTimeDurationLeft;
    let rightArray = this.state.pulseSpaceTimeDurationRight;
    console.log(leftArray[0]);
    nPstd.push(parseInt(leftArray[0]), parseInt(rightArray[0]));
    RNWaveformAudioLib.SendPulseSequence(nFpsi, nAmp_pp, nPstd);
  }

  render() {
    const {
      firstPulseSpaceLeft,
      firstPulseSpaceRight,
      peakToPeakAmplitudeLeft,
      peakToPeakAmplitudeRight,
      pulseSpaceTimeDurationLeft,
      pulseSpaceTimeDurationRight,
    } = this.state;

    return (
      <View style={styles.container}>
      <ScrollView style={styles.scrollViewStyle} horizontal={true}>
        <View style={styles.inputRowContainer}>
          <InputField
            title="firstPulseSpaceLeft"
            value={firstPulseSpaceLeft}
            onChangeText={(value) => { this.updateState('firstPulseSpaceLeft', value); }}
            keyboardType="numeric"
            inRow
          />
          <InputField
            title="firstPulseSpaceRight"
            value={firstPulseSpaceRight}
            onChangeText={(value) => { this.updateState('firstPulseSpaceRight', value); }}
            keyboardType="numeric"
            inRow
          />
        </View>

        <View style={styles.inputRowContainer}>
          <InputField
            title="peakToPeakAmplitudeLeft"
            value={peakToPeakAmplitudeLeft}
            onChangeText={(value) => { this.updateState('peakToPeakAmplitudeLeft', value); }}
            keyboardType="numeric"
            inRow
          />
          <InputField
            title="peakToPeakAmplitudeRight"
            value={peakToPeakAmplitudeRight}
            onChangeText={(value) => { this.updateState('peakToPeakAmplitudeRight', value); }}
            keyboardType="numeric"
            inRow
          />
        </View>

        <View style={styles.inputRowContainer}>
          <InputField
            title="pulseSpaceTimeDurationLeft"
            placeholder="enter: '1, 2, 3, 4, 5'"
            value={pulseSpaceTimeDurationLeft.join(', ')}
            onChangeText={(value) => { this.updateStringToArrayState('pulseSpaceTimeDurationLeft', value); }}
            inRow
          />
          <InputField
            title="pulseSpaceTimeDurationRight"
            placeholder="enter: '1, 2, 3, 4, 5'"
            value={pulseSpaceTimeDurationRight.join(', ')}
            onChangeText={(value) => { this.updateStringToArrayState('pulseSpaceTimeDurationRight', value); }}
            inRow
          />
        </View>

        <Button title="Send" onPress={this.handleSend} />
        </ScrollView>  
      </View>
    );
  }
}
