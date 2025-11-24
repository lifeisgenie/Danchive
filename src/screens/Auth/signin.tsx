import axios from 'axios';
import React, { useState } from 'react';
import {
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
    ScrollView,
    ImageBackground,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import Icon from 'react-native-vector-icons/Ionicons';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

const validatePassword = (password) => {
    const regex = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,16}$/;
    if (!password) return "비밀번호를 입력해주세요.";
    if (!regex.test(password)) return "비밀번호는 8~16자, 영문, 숫자, 특수문자를 포함해야 합니다.";
    return null;
};
const validateEmail = (email) => {
    const regex = /\S+@\S+\.\S+/;
    if (!email) return "이메일을 입력해주세요.";
    if (!regex.test(email)) return "유효한 이메일 형식이 아닙니다.";
    return null;
};

export default function SignInPage({ navigation }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [apiError, setApiError] = useState('');
    const [validationErrors, setValidationErrors] = useState({ email: '', password: '' });
    const [showPassword, setShowPassword] = useState(false);
    const [remember, setRemember] = useState(false);

    const validateForm = () => {
        const emailError = validateEmail(email);
        const passwordError = validatePassword(password);
        setValidationErrors({ email: emailError, password: passwordError });
        return !emailError && !passwordError;
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;
        setIsLoading(true);
        setApiError('');
        try {
            const response = await axios.post(`${API_BASE_URL}/auth/login`, {
                email,
                password,
            });
            if (response.data.success) {
                const { access_token } = response.data.data;
                await AsyncStorage.setItem('userToken', access_token);
                await AsyncStorage.setItem('currentUser', JSON.stringify(user));
                navigation.replace('Tabs', { screen: 'Main' });
            } else {
                setApiError(response.data.message || "로그인에 실패했습니다.");
            }
        } catch (error) {
            if (error.response && error.response.data && error.response.data.message) {
                setApiError(error.response.data.message);
            } else {
                setApiError("서버 연결에 실패했습니다. API 주소를 확인해 주세요.");
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <ImageBackground
            source={require('../assets/login-bg.png')}
            style={styles.bg}
            resizeMode="cover"
        >
            <SafeAreaView style={{ flex: 1 }}>
                <ScrollView contentContainerStyle={styles.scroll}>
                    <View style={styles.card}>
                        <Text style={styles.title}>Login</Text>
                        <Text style={styles.subdesc}>
                            Welcome for login Dankook University{"\n"}
                            graduate art work page
                        </Text>
                        {/* 이메일 */}
                        <TextInput
                            style={[
                                styles.input,
                                !!email && styles.filledInput,
                                validationErrors.email && styles.inputError
                            ]}
                            placeholder="Email"
                            value={email}
                            onChangeText={setEmail}
                            keyboardType="email-address"
                            autoCapitalize="none"
                            placeholderTextColor="#e6f3fd"
                        />
                        {validationErrors.email ? (
                            <Text style={styles.errorText}>{validationErrors.email}</Text>
                        ) : null}
                        {/* 패스워드 */}
                        <View style={[
                            styles.input,
                            styles.inputRow,
                            !!password && styles.filledInput,
                            validationErrors.password && styles.inputError
                        ]}>
                            <TextInput
                                style={styles.passInput}
                                placeholder="Password"
                                value={password}
                                onChangeText={setPassword}
                                secureTextEntry={!showPassword}
                                autoCapitalize="none"
                                placeholderTextColor="#e6f3fd"
                            />
                            <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
                                <Icon name={showPassword ? "eye-off-outline" : "eye-outline"} size={18} color="#b0ccea" />
                            </TouchableOpacity>
                        </View>
                        {validationErrors.password ? (
                            <Text style={styles.errorText}>{validationErrors.password}</Text>
                        ) : null}
                        {/* Remember me */}
                        <TouchableOpacity
                            style={styles.rememberRow}
                            onPress={() => setRemember(!remember)}
                            activeOpacity={0.8}
                        >
                            <View style={[styles.checkbox, remember && styles.checkboxChecked]}>
                                {remember ? (
                                    <Icon name="checkmark-sharp" size={14} color="#fff" />
                                ) : null}
                            </View>
                            <Text style={styles.rememberText}>Remember me</Text>
                        </TouchableOpacity>
                        {apiError ? <Text style={styles.apiErrorText}>{apiError}</Text> : null}
                        {/* 버튼 */}
                        <TouchableOpacity
                            style={styles.loginBtn}
                            onPress={handleSubmit}
                            disabled={isLoading}
                            activeOpacity={0.82}
                        >
                            <Text style={styles.loginBtnText}>
                                {isLoading ? 'Signing in...' : 'login'}
                            </Text>
                        </TouchableOpacity>
                        {/* 하단 링크 */}
                        <View style={styles.linkRow}>
                            <TouchableOpacity onPress={() => navigation.navigate('PasswordResetRequest')}>
                                <Text style={styles.link}>Forgot your password?</Text>
                            </TouchableOpacity>
                            <Text style={styles.sepTxt}>or</Text>
                            <TouchableOpacity onPress={() => navigation.navigate('SignUp')}>
                                <Text style={styles.link}>Sign up</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                </ScrollView>
            </SafeAreaView>
        </ImageBackground>
    );
}

const styles = StyleSheet.create({
    bg: { flex: 1, width: '100%', height: '100%' },
    scroll: {
        flexGrow: 1, justifyContent: 'center', alignItems: 'center',
        paddingVertical: 35,
    },
    card: {
        width: '93%', maxWidth: 410, minHeight: 432,
        backgroundColor: 'rgba(30, 71, 138, 0.22)',
        borderRadius: 15,
        borderWidth: 1.2, borderColor: 'rgba(255,255,255,0.45)',
        paddingHorizontal: 30,
        paddingVertical: 36,
        alignItems: 'flex-start',
        justifyContent: 'flex-start',
        marginTop: 44,
        shadowColor: "#7eb6e9",
        shadowOffset: { width: 0, height: 12 },
        shadowOpacity: 0.10,
        shadowRadius: 19,
        elevation: 6,
    },
    title: {
        fontSize: 28,
        fontWeight: 'bold',
        color: '#fff',
        marginBottom: 7,
        marginLeft: 2,
        textAlign: 'left',
        width: '100%',
        letterSpacing: 0.3
    },
    subdesc: {
        fontSize: 14,
        color: 'rgba(255,255,255,0.75)',
        textAlign: 'left',
        marginBottom: 18,
        width: '100%',
        marginLeft: 2
    },
    input: {
        width: '100%',
        minHeight: 42,
        borderRadius: 8,
        borderWidth: 1.1,
        borderColor: 'rgba(255,255,255,0.50)',
        backgroundColor: 'rgba(255,255,255,0.22)',
        fontSize: 15,
        paddingHorizontal: 14,
        marginTop: 13,
        marginBottom: 2,
        color: "#fff",
        flexDirection: 'row', alignItems: 'center'
    },
    inputRow: { flexDirection: 'row', alignItems: 'center', paddingRight: 5 },
    inputError: {
        borderColor: '#DA4B59',
        backgroundColor: 'rgba(255,0,0,0.14)',
        color: '#ed7575'
    },
    filledInput: {
        backgroundColor: 'rgba(255,255,255,0.28)' // 값 입력시 좀 더 진함
    },
    passInput: { flex: 1, fontSize: 15, color: '#fff', paddingVertical: 7 },
    rememberRow: {
        flexDirection: 'row',
        alignItems: 'center',
        marginTop: 10,
        marginBottom: 18
    },
    checkbox: {
        width: 17, height: 17, borderRadius: 4,
        borderWidth: 1,
        borderColor: '#e2f1fd',
        marginRight: 8, backgroundColor: 'rgba(255,255,255,0.13)', justifyContent: 'center', alignItems: 'center'
    },
    checkboxChecked: { backgroundColor: '#1974b5', borderColor: '#80bffa' },
    rememberText: { fontSize: 13.5, color: '#e2f1fa', fontWeight: '500', marginRight: 9 },
    loginBtn: {
        width: '100%', height: 45, borderRadius: 8,
        backgroundColor: '#fff',
        alignItems: 'center', justifyContent: 'center',
        marginTop: 18,
        marginBottom: 10,
        shadowColor: "#4e83c1",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.11, shadowRadius: 5, elevation: 3,
    },
    loginBtnText: { color: '#1463ab', fontWeight: 'bold', fontSize: 16 },
    apiErrorText: { color: '#da5d5d', textAlign: 'center', marginTop: 6, marginBottom: 0, fontSize: 13 },
    linkRow: {
        flexDirection: 'row', alignItems: 'center', justifyContent: 'center', marginTop: 11,
        width: '100%',
    },
    link: { color: '#e2f1fa', fontSize: 13, fontWeight: '600', marginHorizontal: 7, textDecorationLine: 'underline' },
    sepTxt: { color: "rgba(255,255,255,0.79)", fontWeight: 'bold', fontSize: 13, marginHorizontal: 4 },
    errorText: { color: '#ffdede', fontSize: 12, alignSelf: 'flex-start', marginLeft: 2, marginTop: 1 },
});
