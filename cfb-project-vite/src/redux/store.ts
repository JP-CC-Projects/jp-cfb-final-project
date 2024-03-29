// src/redux/store.ts
import { configureStore } from '@reduxjs/toolkit';
import rootReducer from './reducers/rootReducer';
import { useDispatch } from 'react-redux';

const store = configureStore({
  reducer: rootReducer,
});

console.log('Initial state: ', store.getState());

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export const useAppDispatch = () => useDispatch<AppDispatch>();

export default store;
