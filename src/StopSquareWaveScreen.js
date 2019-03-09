import { Alert, NativeModules } from "react-native";
import {
  Button,
  StyleSheet,
  View,
} from 'react-native';
import React, { Component } from 'react';

import InputField from './components/InputField';

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
    try {
      RNWaveformAudioLib.StopSquareWave(parseFloat(this.state.delayTime));
    } catch (error) {
      Alert.alert(
        'Error',
        error.message,
        [{ text: 'OK', onPress: () => console.log('OK Pressed') }],
      );
    }
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
