import {
  Button,
  Keyboard,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  View,
} from "react-native";

import React from "react";
import SendPulseSequenceScreen from "./src/SendPulseSequenceScreen";
import SendSquareWaveScreen from "./src/SendSquareWaveScreen";
import StopSquareWaveScreen from './src/StopSquareWaveScreen';

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#ddd",
  },
  tabButtonGroup: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  tabButton: {
    flex: 1,
  },
});

export default class App extends React.Component {
  state = {
    focused: 'squareWave',
  };

  focusSquareWave = () => {
    this.setState({
      focused: 'squareWave',
    });
  };

  focusPulseSequence = () => {
    this.setState({
      focused: 'pulseSequence',
    });
  };

  render() {
    const { focused } = this.state;

    const isSquareWave = focused === 'squareWave';

    return (
        <ScrollView style={styles.container}>
          <View>
            <View style={styles.tabButtonGroup}>
              <Button style={styles.tabButton} title="Square Wave" onPress={this.focusSquareWave} disabled={isSquareWave} />
              <Button style={styles.tabButton} title="Pulse Sequence" onPress={this.focusPulseSequence} disabled={!isSquareWave} />
            </View>
            <TouchableOpacity onPress={() => { Keyboard.dismiss(); }}>
              {isSquareWave && (<SendSquareWaveScreen />)}
              {!isSquareWave && (<SendPulseSequenceScreen />)}
            </TouchableOpacity>
            {/* <View style={{ backgroundColor: 'yellow', height: 400 }} /> */}
            {/* <View style={{ backgroundColor: 'green', height: 400 }} /> */}
            <StopSquareWaveScreen />
          </View>
        </ScrollView>
    );
  }
}
