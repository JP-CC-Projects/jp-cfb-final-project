import TeamSearchAutocomplete from '../../common/TeamSearchAutocomplete';
import mainLogo from '../../../assets/mainlogo2.png';
import backgroundVideo from '../../../assets/bgvid.mp4';
import RandomSlogan from './RandomSlogan';


const HomePage = () => {
  return (
    <div className="bg-black flex flex-col h-screen">

      <video autoPlay loop muted playsInline className="absolute w-full h-full object-cover">
        <source src={backgroundVideo} type="video/mp4" />
        Your browser does not support the video tag.
      </video>
      {/* Dark Overlay */}
      <div className="absolute w-full h-full bg-black opacity-70"></div>
      <div className="m-auto z-10 text-center opacity-60">
        {/* Main content */}
        <div className="flex justify-center items-center">
          <div className="flex flex-col justify-center items-center ">
            <img src={mainLogo} alt="Main Logo" className="w-full lg:w-full mb-1" />

          </div>
        </div>
        <RandomSlogan />
        <div className="flex justify-center items-center">
          <TeamSearchAutocomplete />
        </div>
        {/* Buffer to prevent content from stretching */}
        <div className="flex-1"></div>
      </div>
    </div>

  );
};

export default HomePage;


