import PropTypes from 'prop-types';
import React, { Component } from 'react';
import {
  View, Text, TextInput, StyleSheet,
} from 'react-native';

const styles = StyleSheet.create({
  container: {
    padding: 8,
    marginBottom: 8,
  },
  rowContainer: {
    flex: 1,
  },
  input: {
    borderWidth: 1,
    padding: 8,
  },
});

export default class InputField extends Component {
  static propTypes = {
    inRow: PropTypes.bool,
    title: PropTypes.string.isRequired,
  };

  static defaultProps = {
    inRow: false,
  };

  render() {
    const {
      inRow,
      title,
      ...inputProps
    } = this.props;

    return (
      <View style={[
        styles.container,
        inRow && styles.rowContainer,
      ]}
      >
        <Text>{title}</Text>
        <TextInput
          style={styles.input}
          {...inputProps}
        />
      </View>
    );
  }
}
