import { Alert, NativeModules } from "react-native";
import { Button, StyleSheet, View } from "react-native";
import React, { Component } from "react";

import InputField from "./components/InputField";

const RNWaveformAudioLib = NativeModules.RNWaveformAudioLib;

const styles = StyleSheet.create({
  container: {
    // flex: 1
  },
  inputRowContainer: {
    flexDirection: "row"
  },
  itemStyle: {
    // 尺寸
    width: 1000,
    height: 200
  }
});

export default class SendSquareWaveScreen extends Component {
  state = {
    freqLeft: "1000", // NOTE: Unit = kHz
    freqRight: "1000", // NOTE: Unit = kHz
    firstHalfCycleHiLoLeft: "1", // NOTE: 1 = high, 0 = low
    firstHalfCycleHiLoRight: "0", // NOTE: 1 = high, 0 = low
    peaKToPeakAmplitudeLeft: "25", // NOTE: 0 ~ 25, step = 1
    peaKToPeakAmplitudeRight: "25", // NOTE: 0 ~ 25, step = 1
    length: "0" // NOTE: 输出總周期數, 0 ~ 999, 0 = 連續不斷輸出, step = 0.5
  };

  updateState = (key, value) => {
    this.setState({
      [key]: value
    });
  };

  handleSend = () => {
    try {
      console.log('[SQUARE] state =', this.state);
      let freq = new Array();
      let fhchll = new Array();
      let amp = new Array();
      freq.push(parseInt(this.state.freqLeft), parseInt(this.state.freqRight));
      fhchll.push(
        parseInt(this.state.firstHalfCycleHiLoLeft),
        parseInt(this.state.firstHalfCycleHiLoRight)
      );
      amp.push(
        parseFloat(this.state.peaKToPeakAmplitudeLeft),
        parseFloat(this.state.peaKToPeakAmplitudeRight)
      );
      RNWaveformAudioLib.SendSquareWave(
        freq,
        fhchll,
        amp,
        parseFloat(this.state.length)
      );
    } catch (error) {
      Alert.alert(
        'Error',
        error.message,
        [{ text: 'OK', onPress: () => console.log('OK Pressed') }],
      );
    }
  };

  render() {
    const {
      freqLeft,
      freqRight,
      firstHalfCycleHiLoLeft,
      firstHalfCycleHiLoRight,
      peaKToPeakAmplitudeLeft,
      peaKToPeakAmplitudeRight,
      length
    } = this.state;

    return (
      <View style={styles.container}>
        <View style={styles.inputRowContainer}>
          <InputField
            title="Freq Left"
            value={freqLeft}
            onChangeText={value => {
              this.updateState("freqLeft", value);
            }}
            keyboardType="numeric"
            inRow
          />
          <InputField
            title="Freq Right"
            value={freqRight}
            onChangeText={value => {
              this.updateState("freqRight", value);
            }}
            keyboardType="numeric"
            inRow
          />
        </View>
        <View style={styles.inputRowContainer}>
          <InputField
            title="FirstHalfCycleHiLo Left"
            value={firstHalfCycleHiLoLeft}
            onChangeText={value => {
              this.updateState("firstHalfCycleHiLoLeft", value);
            }}
            keyboardType="numeric"
            inRow
          />
          <InputField
            title="FirstHalfCycleHiLo Right"
            value={firstHalfCycleHiLoRight}
            onChangeText={value => {
              this.updateState("firstHalfCycleHiLoRight", value);
            }}
            keyboardType="numeric"
            inRow
          />
        </View>
        <View style={styles.inputRowContainer}>
          <InputField
            title="PeaKToPeakAmplitude Left"
            value={peaKToPeakAmplitudeLeft}
            onChangeText={value => {
              this.updateState("peaKToPeakAmplitudeLeft", value);
            }}
            keyboardType="numeric"
            inRow
          />
          <InputField
            title="PeaKToPeakAmplitude Right"
            value={peaKToPeakAmplitudeRight}
            onChangeText={value => {
              this.updateState("peaKToPeakAmplitudeRight", value);
            }}
            keyboardType="numeric"
            inRow
          />
        </View>
        <InputField
          title="Length"
          value={length}
          onChangeText={value => {
            this.updateState("length", value);
          }}
          keyboardType="numeric"
        />
        <Button title="Send" onPress={this.handleSend} />
      </View>
    );
  }
}
