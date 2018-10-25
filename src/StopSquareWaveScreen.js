import React, { Component } from 'react';
import {
  View,
  Button,
  StyleSheet,
} from 'react-native';
import InputField from './components/InputField';

import { NativeModules } from "react-native";
const RNWaveformAudioLib = NativeModules.RNWaveformAudioLib;

const styles = StyleSheet.create({
  container: {
    // flexDirection: 'row',
  },
});

export default class StopSquareWaveScreen extends Component {
  state = {
    delayTime: '0.9',
  };

  updateState = (key, value) => {
    this.setState({
      [key]: value,
    });
  };

  handleSend = () => {
    console.log(this.state);
    RNWaveformAudioLib.StopSquareWave(parseFloat(this.state.delayTime));
  }

  render() {
    const { delayTime } = this.state;

    return (
      <View style={styles.container}>
        <InputField
          title="Delay Time"
          value={delayTime}
          onChangeText={(value) => { this.updateState('delayTime', value); }}
          keyboardType="numeric"
        />
        <Button title="Send Stop Signal" onPress={this.handleSend} />
      </View>
    );
  }
}
